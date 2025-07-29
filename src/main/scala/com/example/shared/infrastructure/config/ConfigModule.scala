package com.example.shared.infrastructure.config

import com.typesafe.scalalogging.StrictLogging
import com.typesafe.config.ConfigFactory
import com.example.global.infrastructure.http.{DBConfig, HttpConfig}
import com.example.shared.infrastructure.config.Sensitive

/** Reads and gives access to the configuration object.
  */
trait ConfigModule extends StrictLogging {

  lazy val config: Config = {
    val typesafeConfig = ConfigFactory.load()
    
    val dbConfig = DBConfig(
      url = typesafeConfig.getString("db.url"),
      user = typesafeConfig.getString("db.user"),
      password = Sensitive(typesafeConfig.getString("db.password")),
      driver = typesafeConfig.getString("db.driver")
    )
    
    val httpConfig = HttpConfig(
      host = typesafeConfig.getString("api.host"),
      port = typesafeConfig.getInt("api.port")
    )
    
    Config(db = dbConfig, api = httpConfig)
  }

  def loadConfig(): Unit = {
    val info = s"""
                   |Load Configuration:
                   |-----------------------
                   |API: ${config.api}
                   |DB: ${config.db}
                   |...
                   |-----------------
                   |""".stripMargin

    logger.info(info)
  }
}
