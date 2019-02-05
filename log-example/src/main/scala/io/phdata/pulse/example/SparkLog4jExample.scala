/*
 * Copyright 2018 phData Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.phdata.pulse.example

import org.apache.log4j.MDC
import org.apache.spark.{ SparkConf, SparkContext }
import org.slf4j.{ Logger, LoggerFactory }

/**
 * This example shows how to
 * 1) initialize a logger
 * 2) write the application id and hostname to the driver and executors MDC so they will be
 * searchable in the solr index.
 */
object SparkLog4jExample {

  private implicit val log: Logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    log.info("Starting up the spark logging example")
    val conf = new SparkConf().setAppName("Pulse Spark Logging Example")
    val sc   = SparkContext.getOrCreate(conf)

    run(sc, numEvents = 10000)
    sc.stop()
    import org.apache.log4j.LogManager
    LogManager.shutdown()
  }

  def run(sc: SparkContext, numEvents: Int): Unit = {
    val testData = 1 to numEvents
    val testRdd  = sc.parallelize(testData)

    PulseProfiler.time("metric_rdd_foreach") { () =>
      testRdd.foreach { num =>
        if (num % 10000 == 0) {
          log.error(s"XXXXX error! num: " + num)
        } else if (num % 5000 == 0) {
          log.warn(s"XXXXX warning! num: " + num)
        } else {
          log.info(s"XXXXX found: " + num)
        }
      }
    }

    PulseProfiler.measure("metric_count", testRdd.count())

    log.info("Shutting down the spark logging example")
  }
}

object PulseProfiler {
  def time[E](tag: String)(func: () => E)(implicit logger: Logger): E = {
    val start   = System.currentTimeMillis()
    val result  = func()
    val elapsed = System.currentTimeMillis() - start
    measure(tag, elapsed.toString)
    result
  }

  def measure(tag: String, value: String)(implicit logger: Logger): Unit = {
    MDC.put(tag, value)
    println(s"tag: $tag, value: $value")
    logger.info("metric")
    MDC.remove(tag)
  }

  def measure(tag: String, value: Long)(implicit logger: Logger): Unit =
    measure(tag, value.toString)
}
