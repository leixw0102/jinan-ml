package com.ehl.ml

import com.ehl.tvc.lxw.common.EhlConfiguration
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by 雷晓武 on 2017/9/26.
  * 取5分钟 获取当前
  */
class Conf(ehlConfiguration: EhlConfiguration) {
    val min5=ehlConfiguration.getInt("window.length",5)
  def verify(): Boolean ={
    min5/2>offsetMin
  }

  val runningMode = ehlConfiguration.getOption("running.mode")

  val confStartDateTime=ehlConfiguration.getOption("start.datetime")
  val confEndDateTime = ehlConfiguration.getOption("end.datetime")

  val redisIp = ehlConfiguration.get("redis.ip")
  val redisPort = ehlConfiguration.getInt("redis.port",6379)
  val totalKM = ehlConfiguration.getLong("total.km",3411L)

  val isTest=(confStartDateTime!=None && !confStartDateTime.get.isEmpty) && confStartDateTime.get!=null
  val existEndDateTime =confEndDateTime!=None && !(confEndDateTime.get.isEmpty) && confEndDateTime.get != null

  /**
    * 测试用
    * @return
    */
  def testEndDateTime = {
    if(existEndDateTime) {
      DateTime.parse(confEndDateTime.get + " 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    }else{
      DateTime.now()
    }

  }

  val offsetMin = ehlConfiguration.getInt("offset.min",1)
  val esIndex = ehlConfiguration.get("index")
  val esType = ehlConfiguration.get("type")

  val esResource = esIndex+"/"+esType
  @Deprecated
  def startDateTime={
    if(confStartDateTime.isEmpty || confStartDateTime == null) {
      val dateTimeStr = DateTime.now().withSecondOfMinute(0)
      if (dateTimeStr.getMinuteOfHour % 5 == 0) dateTimeStr else get5MinDivDateTime(dateTimeStr)
    }else{
      DateTime.parse(confStartDateTime+" 00:00:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    }
  }

  def confDateTime =  DateTime.parse(confStartDateTime.get+" 00:00:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))


  def autoDateTime = {
    val dateTimeStr = DateTime.now().withSecondOfMinute(0)
    if (dateTimeStr.getMinuteOfHour % 5 == 0) dateTimeStr else get5MinDivDateTime(dateTimeStr)
  }


  /**
    * redis 数据操作
    */
    def redis(): Unit ={

    }

  /**
    *
    * @param dateTime
    * @return
    */
    private def get5MinDivDateTime(dateTime:DateTime): DateTime ={
      val millis = dateTime.getMillis-dateTime.getMillis%(min5*60*1000)
      new DateTime(millis)
    }
}

object Conf{
  def apply(ehlConfiguration: EhlConfiguration): Conf = new Conf(ehlConfiguration)

  def main(args: Array[String]) {
    println(true || false)
//    val ehlConfiguration = new EhlConfiguration("business.properties")
//    val conf = Conf(ehlConfiguration)
//    println(conf.isTest)
//    ehlConfiguration.foreach()
//    val s = DateTime.parse("2017-09-11 12:12:12",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
//    println(s.getMillis+"\t"+s.toDate.getTime+"\t"+System.currentTimeMillis())
//    println(new DateTime((s.getMillis-s.getMillis%(5*60*1000))).toString("yyyy-MM-dd HH:mm:ss"))
//    println(DateTime.now().withSecondOfMinute(0).toString("yyyy-MM-dd HH:mm:ss"))
  }
}
