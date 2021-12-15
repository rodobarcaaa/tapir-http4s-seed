package com.example.global.infrastructure.config

import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

import scala.collection.immutable.TreeMap

/** Reads and gives access to the configuration object.
  */
trait ConfigModule extends StrictLogging {

  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]

  def loadConfig(): Unit = {
    val baseInfo = s"""
                   |Configuration:
                   |-----------------------
                   |API: ${config.api}
                   |
                   |Build & env info:
                   |-----------------
                   |""".stripMargin

    val info = TreeMap(Map.empty[String, scala.Any].toSeq: _*).foldLeft(baseInfo) { case (str, (k, v)) =>
      str + s"$k: $v\n"
    }

    logger.info(info)
  }
}
