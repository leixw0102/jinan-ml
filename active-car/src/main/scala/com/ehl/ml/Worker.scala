package com.ehl.ml

import org.apache.log4j.Logger
import org.joda.time.DateTime

/**
  * Created by 雷晓武 on 2017/9/27.
  */
class Worker(esQuery: EsQuery,conf: Conf) {
  val logger = Logger.getLogger(getClass)

  def getRedisKeyFiled2Long(key:String):Long = {
    val tmpKey = RedisUtils.getFiled2ValueWithHash(key)
    if(tmpKey.isEmpty ||tmpKey ==null) 1000L else tmpKey.toLong
  }

  def getRedisKeyFiled1Long(key:String):Long = {
    val tmpKey = RedisUtils.getFiled1ValueWithHash(key)
    if(tmpKey.isEmpty ||tmpKey ==null) 1000L else tmpKey.toLong
  }

  def work(from:DateTime,end:DateTime): Unit ={
    val esResult = esQuery.query(from.getMillis,end.getMillis)
    if(null == esResult) {
      logger.info("search es null or exception ")
      return
    };
    val redisKey = RedisUtils.getRedisKey(from,end)
    logger.info("rediskey = "+redisKey)


    val hisDistinct4Day2CountAvg = getRedisKeyFiled2Long(redisKey)
    if(hisDistinct4Day2CountAvg==0){
      logger.info("active history service is not finished,you may waiting for it to finish")
      return
    }
    val carCurCount = esResult.count()

    //参照老版本
    if(carCurCount != 0) {
      //3
      val λ = esResult.distinct().count() / hisDistinct4Day2CountAvg.toFloat
      //2
      val hisDistinct4Day2Count = getRedisKeyFiled1Long(redisKey)
      val totalVehicleCount = λ * hisDistinct4Day2Count
      //1
      //      logger.info("redis key ="+redisKey+"\t"+hisDistinct4Day2CountAvg +"\t"+hisDistinct4Day2Count)
      val value =  s"{'recordTime':${end.getMillis},'createTime':${System.currentTimeMillis()},'avgVehCount':${(totalVehicleCount/conf.totalKM.toFloat).formatted("%.2f")},'activeVehCount':${carCurCount}}"
      println(value)

      RedisUtils.saveRedis("activecarinfo_" + end.getMillis,value)
    }else{

      println(
        s""" recordTime=${from.toString("yyyy-MM-dd HH:mm:ss")}
            |  createTime=${DateTime.now().toString("yyyy-MM-dd HH:mm:ss")}
            |  activeVehCount=${}
          """.stripMargin)

    }
  }
}

object Worker extends Serializable{
  def apply(esQuery: EsQuery, conf: Conf): Worker = new Worker(esQuery, conf)
}
