package com.ehl.ml

import com.ehl.tvc.lxw.common.EhlConfiguration
import com.ehl.tvc.lxw.core.offline.AbstractSparkEhl
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{ SQLContext}

/**
  * Created by 雷晓武 on 2017/9/19.
  */
object History extends AbstractSparkEhl with App{
  override def getSparkAppName: String = "active-history-service"

  /**
    * 参数读入
    * spark 独立模式
    * yarn client /yarn cluster 都支持
    * @return
    */
  override def initEhlConfig: EhlConfiguration = {
    new EhlConfiguration()
  }


  operateSpark(args ,ehlConf )(op=>{

    //init
    val params = Params.apply(ehlConf)
    val sQLContext = new SQLContext(op);
    val esQuery = EsQuery.apply(sQLContext,params.esResource,getESConfig(ehlConf));
    JedisUtil.init(params.redisIp,params.redisPort);
    /**
      * 逻辑块
      */
    val yesterDay = params.dayZero()
    //昨天一天数据按点拆分
    for(j <- 0L to params.gradingSize-1 ) {
//    for(j<- 0L to 2){
      //累加 key tmpDate
      val tmpDate = yesterDay.plusMinutes(j.toInt*params.gradingTimeMins)

      val redisKey = RedisUtils.getRedisKey(tmpDate,params.gradingTimeMins)
      var historyRecordsTmp1:RDD[String] = null
      var historyRecordsTmp2:RDD[String] = null;
      for (i <- 0 to params.historySize-1) {
        val end = tmpDate.plusDays(-i)
        val from = end.plusMinutes(-params.gradingTimeMins);
        /**
          * es search
          */
          logger.info("from={} end ={}",from.getMillis,end.getMillis)
        val esResult = esQuery.query(from.getMillis,end.getMillis)
        esResult.cache()
        //算法实现
        if(null != historyRecordsTmp1){
          historyRecordsTmp1 = historyRecordsTmp1.union(esResult.distinct())
        }else{
          historyRecordsTmp1=esResult.distinct()
        }

        if(null == historyRecordsTmp2){
          historyRecordsTmp2 = esResult
        }else{
          historyRecordsTmp2 =historyRecordsTmp2.union(esResult)
        }
      }

//      历史三个月同时刻去重车总数平均值（先去重，再求和，取平均）
        val hisDistinct4Day2CountAvg = historyRecordsTmp1.count()/params.historySize
        val hisDistinct4All2Count = historyRecordsTmp2.distinct().count()
        println("value..."+redisKey+"\t"+hisDistinct4All2Count+"\t"+hisDistinct4Day2CountAvg)
//      saveRedis(redisKey)
        RedisUtils.saveFiled1Redis2Hash(redisKey,hisDistinct4All2Count+"")
        RedisUtils.saveFiled2Redis2Hash(redisKey,hisDistinct4Day2CountAvg+"")
      //历史三个月同时刻车辆总数（对应时间点三个月的上路车辆总数；先求和，再去重）
    }
  })





}
