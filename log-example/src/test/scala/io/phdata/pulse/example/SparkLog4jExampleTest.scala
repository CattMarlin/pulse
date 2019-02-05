/* Copyright 2018 phData Inc. */

package io.phdata.pulse.example

import java.util.logging.LogManager

import org.apache.log4j.Appender

import scala.collection.JavaConverters._
import org.apache.spark.{ SparkConf, SparkContext }
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory

class SparkLog4jExampleTest extends FunSuite {
  test("Run test job") {
    val conf = new SparkConf().setMaster("local[*]").setAppName("Pulse Spark Logging Example")
    val sc   = SparkContext.getOrCreate(conf)

    SparkLog4jExample.run(sc, 1)

    sc.stop()

//    org.apache.log4j.Logger.getRootLogger.getAllAppenders.asScala
//      .map(_.asInstanceOf[Appender]) foreach {
//      case x if x.getName == "http" => x.close()
//      case _                        => () // keep the console appender open just in case another message comes through
//    }
  }
}
