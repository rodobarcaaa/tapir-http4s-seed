package com.example

import com.example.books.BookModule
import com.example.global.infrastructure.http._
import com.example.shared.infrastructure.config.Config
import com.softwaremill.macwire._

trait MainModule extends BookModule {

  def config: Config

  lazy val dbConfig: DBConfig     = DBConfig(config.db.driver, config.db.url, config.db.user, config.db.password)
  lazy val httpConfig: HttpConfig = HttpConfig(config.api.host, config.api.port)

  lazy val metricsApi: MetricsApi = wire[MetricsApi]
  lazy val httpApi: HttpApi       = wire[HttpApi]
}

object MainModule {

  def initialize: MainModule = {
    val initModule = new InitModule {}
    initModule.initialize()
    new MainModule { override def config: Config = initModule.config }
  }

}
