package org.project.modules.log

import org.apache.spark.Logging
import org.apache.log4j.{ Level, Logger }

object SparkLog extends Logging {

  def setLogLevel(level:Level) {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      Logger.getRootLogger.setLevel(level)
    }
  }
}