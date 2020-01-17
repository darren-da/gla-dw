package com.atgla.dw.realtime.app

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import com.alibaba.fastjson.JSON
import com.atgla.dw.GmallConstants
import com.atgla.dw.realtime.bean.StartupLog
import com.atgla.dw.realtime.utils.{EsUtil2, MyKafkaUtil}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import redis.clients.jedis.Jedis

import scala.collection.mutable.ListBuffer

object RealtimeStartupApp {
  
  def main(args: Array[String]): Unit = {
    val sparkConf: SparkConf = new SparkConf().setAppName("realtime_startup").setMaster("local[*]")
    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sc, Seconds(5))
    val recordStreaming: InputDStream[ConsumerRecord[String, String]] = MyKafkaUtil.getKafkaStream(GmallConstants.ES_INDEX_DAU, ssc)

    //    recordStreaming.foreachRDD(rdd=>
    //      println(rdd.map(_.value()).collect().mkString("\n"))
    //    )
    val startupStringDstream: DStream[String] = recordStreaming.map(_.value())
    val startUpDstream: DStream[StartupLog] = recordStreaming.map(_.value()).map { json =>
      val startUpLog: StartupLog = JSON.parseObject(json, classOf[StartupLog])
      startUpLog

    }
    //1   先做过滤  把redis中的数据与当前批次的数据进行对比 过滤掉已有的数据
    val filteredDstream: DStream[StartupLog] = startUpDstream.transform { rdd =>
      println(s" 过滤前 ：rdd.count() = ${rdd.count()}")
      val jedis = new Jedis(" node101", 6379)
      val dauSet: util.Set[String] = jedis.smembers("dau:" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()))
      jedis.close()
      val dauBC: Broadcast[util.Set[String]] = sc.broadcast(dauSet)
      val filteredRDD: RDD[StartupLog] = rdd .filter { startuplog =>
        !dauBC.value.contains(startuplog.mid)
      }

      println(s" 过滤后 ：rdd.count() = ${filteredRDD.count()}")
      filteredRDD
    }



    //2   把新活跃用户的数据保存到redis

    filteredDstream.foreachRDD { rdd =>

      rdd.foreachPartition { startupItr =>

        val jedis = new Jedis("node101", 6379) //driver
      val list = new ListBuffer[StartupLog]()
        startupItr.foreach { startupLog =>
          val datetimeString: String = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(startupLog.ts))
          val datetimeArray: Array[String] = datetimeString.split(" ")
          val dateString: String = datetimeArray(0)
          val timeArray: Array[String] = datetimeArray(1).split(":")
          val hour: String = timeArray(0)
          val minute: String = timeArray(1)

          val key = "dau:" + dateString
          jedis.sadd(key, startupLog.mid)

          //补充 一些时间字段 用户 es中的时间分析
          startupLog.logDate=dateString
          startupLog.logHour=hour
          startupLog.logHourMinute=hour+":"+minute


          list+=startupLog
        }
        jedis.close()
        EsUtil2.executeIndexBulk(GmallConstants.ES_INDEX_DAU,list.toList," ")


      }
    }


    //3   保存到es中


    ssc.start()
    ssc.awaitTermination()

  }


}