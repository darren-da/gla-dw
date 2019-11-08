package com.atgla.dw.realtime.app

import com.alibaba.fastjson.JSON
import com.atgla.dw.GmallConstants
import com.atgla.dw.realtime.bean.OrderInfo
import com.atgla.dw.realtime.utils.MyKafkaUtil
import org.apache.hadoop.conf.Configuration
import org.apache.phoenix.spark._
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}

object OrderApp {
  def main(args: Array[String]): Unit = {
    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("order_app")
    val ssc = new StreamingContext(sparkConf, Seconds(5))

    //val inputDstream:InputDStream[ConsumerRecord[String,String]] = MyKafkaUtil.getKafkaStream(GmallConstants.KAFKA_TOPIC_ORDER,ssc)

    //次数开启了  Scala自动生成变量，变量自动生成
    val inputDstream = MyKafkaUtil.getKafkaStream(GmallConstants.KAFKA_TOPIC_ORDER, ssc)

    //测试消费
    /* inputDstream.map(_.value()).foreachRDD(rdd =>
       println(rdd.collect().mkString("\n"))
     )*/

    val orderInfoDstrearm: DStream[OrderInfo] = inputDstream.map {
      _.value()
    }.map { orderJson =>
      val orderInfo: OrderInfo = JSON.parseObject(orderJson, classOf[OrderInfo])
      //日期     eg:2019-11-07 16:23:38
      val createTimeArr = orderInfo.create_time.split(" ")
      orderInfo.create_date = createTimeArr(0)
      val timeArr = createTimeArr(1).split(":")
      orderInfo.create_hour = timeArr(0)
      //收件人电话  脱敏处理

      //方法1（字符串的截取）：var showPhone = phone.substr(0,3)+'’+phone.substr(7);
//      orderInfo.consignee_tel = "*******" +
//        orderInfo.consignee_tel.splitAt(7)._2
      val tel=orderInfo.consignee_tel

      orderInfo.consignee_tel=tel.substring(0,3) + "****"+ tel.substring(7)

      orderInfo
    }

    orderInfoDstrearm.foreachRDD({
      rdd =>
        val configuration = new Configuration()
        println(rdd.collect().mkString("\n"))
        rdd.saveToPhoenix("GMALL2019_ORDER_INFO", Seq(

          "ID", "PROVINCE_ID", "CONSIGNEE",
          "ORDER_COMMENT", "CONSIGNEE_TEL",
          "ORDER_STATUS", "PAYMENT_WAY", "USER_ID",
          "IMG_URL", "TOTAL_AMOUNT", "EXPIRE_TIME",
          "DELIVERY_ADDRESS", "CREATE_TIME", "OPERATE_TIME",
          "TRACKING_NO", "PARENT_ORDER_ID", "OUT_TRADE_NO", "TRADE_BODY",
          "CREATE_DATE", "CREATE_HOUR"), configuration,
          Some("node101,node102,node103:2181")
        )

    })

    ssc.start()
    ssc.awaitTermination()


  }

}
