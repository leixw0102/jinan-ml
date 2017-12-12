package com.ehl.ml

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SQLContext
import org.elasticsearch.spark.sql._

/**
  * Created by 雷晓武 on 2017/9/25.
  */
object EsQuery {
  def apply(sQLContext: SQLContext, resource: String,cfg:Map[String,String]): EsQuery = new EsQuery(sQLContext, resource,cfg)
}

class EsQuery(sQLContext: SQLContext,resource: String,cfg:Map[String,String])extends Serializable{
  def query(from:Long,end:Long):RDD[String]={
    //es 结果
    try {
      val esResult = sQLContext.esDF(resource, cfg)
        .select("timestamp", "car_plate_number", "car_plate_type")
        .filter("car_plate_number is not null").filter("car_plate_type is not null")
        .filter("timestamp>=" + from + " and timestamp <" + end)
        //          .filter($"timestamp".between(from.getMillis,end.getMillis)).select("timestamp","car_plate_number" , "car_plate_type")
        .map(x => {
        x.getAs[String]("car_plate_number").toUpperCase() + "-" + x.getAs[Int]("car_plate_type")
      })
      esResult
    }catch {
      case e:Exception=>e.printStackTrace();null;
    }

  }
}
