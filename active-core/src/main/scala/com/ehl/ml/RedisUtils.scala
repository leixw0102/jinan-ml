package com.ehl.ml

import org.joda.time.DateTime

/**
  * Created by 雷晓武 on 2017/9/25.
  */
object RedisUtils {
//  val key ="ehl-car-history"
  val filed1="hisDistinct4All2Count"
  val filed2="hisDistinct4Day2CountAvg"
  def saveRedis2Hash(key:String,filed:String,value:String): Unit ={
    val jedis = JedisUtil.getJedis;
    jedis.select(2)
    try{
      jedis.hset(key,filed,value)
    }catch {
      case e:Exception =>e.printStackTrace()
    }finally {
      JedisUtil.returnResource(jedis)
    }
  }
//
//  /**
//    * from-to
//    * format:HH:mm:ss
//    *
//    * @param time
//    * @param intervalMin
//    * @return
//    */
  def getRedisKey(time:DateTime,intervalMin:Int)={
    time.toString("HH:mm:ss")+"-"+time.plusMinutes(intervalMin).toString("HH:mm:ss")
  }

  def getRedisKey(from:DateTime,end:DateTime)={
      from.toString("HH:mm:ss")+"-"+end.toString("HH:mm:ss")
  }


  def getValueWithHash(key:String,filed:String):String={
    val jedis = JedisUtil.getJedis;
    jedis.select(2)
    try{
      return jedis.hget(key,filed)
    }catch {
      case e:Exception =>e.printStackTrace();return null
    }finally {
      JedisUtil.returnResource(jedis)
    }
  }

  def getFiled1ValueWithHash(key:String)=getValueWithHash(key,filed1)

  def getFiled2ValueWithHash(key:String)=getValueWithHash(key,filed2)
  /**
    *
    * @param value
    */
  def saveFiled1Redis2Hash(key:String,value:String): Unit ={
    saveRedis2Hash(key,filed1,value)
  }

  def saveFiled2Redis2Hash(key:String,value:String): Unit ={
    saveRedis2Hash(key,filed2,value)
  }

  /**
    *
    * @param k
    * @param value
    * @return
    */
  def saveRedis( k:String, value:String):Boolean={
    //    val key=k;
    val jedis = JedisUtil.getJedis;
    jedis.select(2)
    try{
      jedis.setex(k,2*24*60*60,value);
      true;
    }catch {
      case e:Exception =>e.printStackTrace()
        false;
    }finally {
      JedisUtil.returnResource(jedis)
    }
  }

}
