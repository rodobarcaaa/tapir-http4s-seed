package com.example.shared.infrastructure.config

import com.typesafe.scalalogging.StrictLogging
import pureconfig.ConfigSource
import pureconfig.generic.auto._

/** Reads and gives access to the configuration object.
  */
trait ConfigModule extends StrictLogging {

  lazy val config: Config = ConfigSource.default.loadOrThrow[Config]

  def loadConfig(): Unit = {
    val info = s"""
                   |Configuration:
                   |-----------------------
                   |API: ${config.api}
                   |...
                   |-----------------
                   |""".stripMargin

    logger.info(info)
  }
}
