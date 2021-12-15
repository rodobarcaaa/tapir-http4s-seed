package com.example

import com.example.books.BookModule
import com.example.global.infrastructure.config.Config
import com.example.global.infrastructure.http.{HttpApi, MetricsApi}
import com.example.shared.infrastucture.http.HttpConfig

trait MainModule extends BookModule {
  import com.softwaremill.macwire._

  def config: Config

  lazy val httpConfig: HttpConfig = HttpConfig(config.api.host, config.api.port)
  lazy val metricsApi: MetricsApi = wire[MetricsApi]
  lazy val httpApi: HttpApi       = wire[HttpApi]
}
