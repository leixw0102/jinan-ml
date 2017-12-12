package com.ehl.ml

import java.text.DateFormat

import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, Minutes}

/**
 * @author ${user.name}
 */
object App {
  
  def foo(x : Array[String]) = x.foldLeft("")((a,b) => a + b)
  
  def main(args : Array[String]) {
//    println( "Hello World!" )
//    println("concat arguments = " + foo(args))
    println(3/3471)
//    val t1=DateTime.parse("2017-09-27 10:12:12",DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"))
//    val a = Minutes.minutesBetween(t1,DateTime.now()).getMinutes
//    println(a)
  }

}
