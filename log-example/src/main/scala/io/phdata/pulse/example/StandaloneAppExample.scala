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

import org.apache.log4j.{ Appender, Logger, MDC, NDC }

object StandaloneAppExample {
  private val log = Logger.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    MDC.put("hostname", java.net.InetAddress.getLocalHost().getHostName())
    NDC.push("ndc message")
    val numEvents   = args(0).toInt
    val sleepMillis = args(1).toInt
    0 to numEvents map { num =>
      val uuid = java.util.UUID.randomUUID.toString()
      log.info(s"info message $uuid")
      Thread.sleep(sleepMillis)
      if (num % 300 == 0) {
        log.error(s"error happened $uuid", new Exception())
      }
    }
    try {
      throw new Exception("exiting")
    } catch {
      case e: Throwable =>
        import scala.collection.JavaConverters._
        org.apache.log4j.Logger.getRootLogger.getAllAppenders.asScala
          .map(_.asInstanceOf[Appender]) foreach {
          case x if x.getName == "http" => x.close()
          case _                        => () // keep the console appender open just in case another message comes through
        }
    }
  }
}
