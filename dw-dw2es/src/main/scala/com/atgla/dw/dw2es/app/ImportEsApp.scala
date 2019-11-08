package com.atgla.dw.dw2es.app

import com.atgla.dw.dw2es.bean.SaleDetailDaycount
import com.atgla.dw.dw2es.utils.MyEsUtil
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ListBuffer

object ImportEsApp {
  def main(args: Array[String]): Unit = {
    //0 准备环境
    val sparkConf:SparkConf = new SparkConf().setMaster("localhost[*]").setAppName("gla_dw2es")
    val sparkSession: SparkSession = SparkSession.builder().config(sparkConf).enableHiveSupport().getOrCreate()

    //1 获取宽表信息 sql => RDD[bean]
    sparkSession.sql("use kettle_dw")
    val saleRDD:RDD[SaleDetailDaycount] = sparkSession.sql("select user_id," +
      "sku_id," +
      "user_gender," +
      "cast(user_age as int) user_age," +
      "user_level,cast( sku_price as double) " +
      "sku_price,sku_name,sku_tm_id, " +
      "sku_category3_id," +
      "sku_category2_id," +
      "sku_category1_id," +
      "sku_category3_name," +
      "sku_category2_name," +
      "sku_category1_name," +
      "spu_id,sku_num," +
      "cast(order_count as bigint) order_count," +
      "cast(order_amount as double) order_amount," +
      "dt from dws_sale_detail_daycount" +
      " where dt='2019-10-01'").as[SaleDetailDaycount].rdd
    //2 把rdd存入es
    //    saleRDD.foreachPartition{saleItr=>
    //
    //      MyEsUtil.insertBulk(GmallConstant.ES_INDEX_SALE,saleItr.toList)
    //    }
    saleRDD.foreachPartition{saleDetailItr=>
      val saleDetailDaycountList = new ListBuffer[SaleDetailDaycount]()
      for (saleDetail <- saleDetailItr){
        saleDetailDaycountList+=saleDetail
        if (saleDetailDaycountList.size>0&&saleDetailDaycountList.size%10==0){


        }
      }

    }




  }

}
