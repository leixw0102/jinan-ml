package com.ehl.ml

import com.ehl.tvc.lxw.common.EhlConfiguration
import com.ehl.tvc.lxw.core.offline.AbstractSparkEhl
import org.apache.spark.sql.SQLContext
import org.joda.time.{DateTime, Minutes}

/**
  * Created by 雷晓武 on 2017/9/26.
  *
  * 1	当前每公里平均车辆数=当前路网所有车辆数/路网总里程
  * 2 当前路网所有车辆数=λ*历史三个月同时刻车辆总数（对应时间点三个月的上路车辆总数；先求和，再去重）；
  * 引入车辆总数修正因子λ，
  * 3λ=(当前全部卡口拍到的上路车总数（去重）)/(历史三个月同时刻去重车总数平均值（先去重，再求和，取平均）)
  *
  *
  * input 通行过车数据、路网总里程数据。
  * 历史三个月同时刻去重车总数平均值（先去重，再求和，取平均）)  ===a  在redis中取
  *
  * 历史三个月同时刻车辆总数（对应时间点三个月的上路车辆总数；先求和，再去重）；==b  在Redis中取
  *
  * 路网总里程 ==c (济南3411)
  *
  */
object ActiveCar extends AbstractSparkEhl with App{
  override def getSparkAppName: String = "active-car"

  /**
    *
    * @return
    */
  override def initEhlConfig: EhlConfiguration = {
    new EhlConfiguration()
  }

  /**
    *
    */
  operateSpark(args,ehlConf)(op=>{
    //init
    val conf = Conf(ehlConf)
    if(!conf.verify()){
      new RuntimeException(s"window.length=${conf.min5};offset = ${conf.offsetMin} ,condition = window.length/2>offset")
    }
    val sqlContent = new SQLContext(op)
    val esQuery = EsQuery.apply(sqlContent,conf.esResource,getESConfig(ehlConf));
    JedisUtil.init(conf.redisIp,conf.redisPort.toString)
    // 当前时间
    val worker = Worker(esQuery,conf)


    if(conf.isTest){
      //循环到当前时间
      var tmpDateTime = conf.confDateTime
      while (Minutes.minutesBetween(tmpDateTime, conf.testEndDateTime).getMinutes >4) {

        logger.info(Minutes.minutesBetween(tmpDateTime, conf.testEndDateTime).getMinutes+"==========")
        val end = tmpDateTime.plusMinutes(conf.min5)
        logger.info("test ===="+tmpDateTime.toString("yyyy-MM-dd HH:mm:ss") +"\t" + end.toString("yyyy-MM-dd HH:mm:ss"))
          worker.work(tmpDateTime,end)
        tmpDateTime = end
      }

    }else{
      var startTime = conf.autoDateTime
      conf.runningMode match {
        case Some("forever") =>{
          while (true) {
            executor(startTime, conf, worker)

            startTime = startTime.plusMinutes(conf.min5)
            logger.info("change startTime = " + startTime.toString("yyyy-MM-dd HH:mm:ss"))
          }
        }
        case _=>{
          executor(startTime,conf,worker)
        }
      }

    }

    //other
//    val startTime = conf.autoDateTime
//    val currentDateTime = DateTime.now()
//    if(currentDateTime.getMinuteOfHour-startTime.getMinuteOfHour<conf.offsetMin){
//      println(
//        s"""currentDateTime = ${currentDateTime.toString("yyyy-MM-dd HH:mm:ss")} ;
//            | startTime = ${startTime.toString("yyyy-MM-dd HH:mm:ss")}
//            | sleepMinTime = ${conf.offsetMin}""".stripMargin)
//      Thread.sleep(conf.offsetMin*60*1000)
//    }
//    单次执行
//    val end = startTime.plusMillis(-conf.min5)
//    worker.work(startTime,end,-conf.min5)
//

  })

  /**
    *
    * @param startTime
    * @param conf
    * @param worker
    * @return
    */
  def executor(startTime:DateTime,conf: Conf,worker: Worker)={
    var currentDateTime = DateTime.now()
    logger.info("start  currentDateTime="+currentDateTime.toString("yyyy-MM-dd HH:mm:ss") +" \t startTime="+ startTime.toString("yyyy-MM-dd HH:mm:ss"))
    while (currentDateTime.getMillis<startTime.getMillis ||
      currentDateTime.getMinuteOfHour-startTime.getMinuteOfHour<conf.offsetMin){
      println(
        s"""currentDateTime = ${currentDateTime.toString("yyyy-MM-dd HH:mm:ss")} ;
            | startTime = ${startTime.toString("yyyy-MM-dd HH:mm:ss")}
            | sleepMinTime = ${conf.offsetMin}""".stripMargin)
      Thread.sleep(conf.offsetMin*60*1000)

      currentDateTime = DateTime.now()
      logger.info("await and new currentDateTime = "+currentDateTime.toString("yyyy-MM-dd HH:mm:ss"))
    }
    //单次执行
    val end = startTime.plusMinutes(-conf.min5)

    logger.info("startTime = "+startTime.toString("yyyy-MM-dd HH:mm:ss") +"\t"+"endTime = "+end.toString("yyyy-MM-dd HH:mm:ss") +"\t"+"window.length = "+(-conf.min5))

    worker.work(end,startTime)
  }

//  def getEndDateTime(conf:Conf) ={
//    if(conf.existEndDateTime){
//
//    }else{
//      DateTime.now()
//    }
//  }
}
