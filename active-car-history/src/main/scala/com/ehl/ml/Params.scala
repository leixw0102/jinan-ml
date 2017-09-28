package com.ehl.ml

import com.ehl.tvc.lxw.common.EhlConfiguration
import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

/**
  * Created by 雷晓武 on 2017/9/19.
  */
class Params(ehlConfiguration: EhlConfiguration) {

  val redisIp = ehlConfiguration.get("redis.ip")
  val redisPort=ehlConfiguration.get("redis.port")

  val historySize = ehlConfiguration.getInt("history.size",90) // 历史天数
  val historyStartDate=ehlConfiguration.getOption("history.startDateTime")
  val gradingTimeMins = ehlConfiguration.getInt("grading.time.min",5)
  def dayZero():DateTime ={
    if(!historyStartDate.isEmpty && historyStartDate.get!=null){
      DateTime.parse(historyStartDate.get+" 00:00:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    }else{
      new DateTime().plusDays(-1).toDateTime.withMillisOfDay(0)
    }
  }

  def startTime2LonOfDay=0L
  def endTime2LongOfDay=24*60 //分钟

//  def startTimeOfDay=DateTime.now().withMillisOfDay(0)

  val esIndex = ehlConfiguration.get("index")
  val esType = ehlConfiguration.get("type")

  val esResource = esIndex+"/"+esType


  val divSize = endTime2LongOfDay % gradingTimeMins //day2nao%gradingTimeMins

  val gradingSize = if(divSize==0) endTime2LongOfDay/gradingTimeMins else endTime2LongOfDay /gradingTimeMins +1//if(divSize ==0) day2nao/gradingTimeMins else day2nao/gradingTimeMins+1


  override def toString()=esResource+"\t"+gradingTimeMins+"\t"+redisIp+"\t"+redisPort

}

object Params {

  def apply(ehlConfiguration: EhlConfiguration):
  Params = new Params(ehlConfiguration)
  def main(args: Array[String]) {
    val a = DateTime.parse("2017-09-09 00:00:00",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
    println(a.toString("yyyy-MM-dd HH:mm:ss"))
//    println(DateTime.parse("23:59:59",DateTimeFormat.forPattern("HH:mm:ss")))
//    println(DateTime.now().withMonthOfYear(0).withHourOfDay(24).toString("HH:mm:ss"))
//    println(a.toDate.getTime)
  }
}
