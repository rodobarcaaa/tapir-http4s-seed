package com.example

import cats.effect.{IO, Resource}
import com.example.auth.AuthModule
import com.example.books.BookModule
import com.example.global.infrastructure.http._
import com.example.shared.infrastructure.config.ConfigModule
import com.softwaremill.macwire._
import io.prometheus.client.hotspot

/** To Initialised resources needed by the application to start.
  */
trait MainModule extends ConfigModule with BookModule with AuthModule {
  def initialize(): MainModule = {
    loadConfig()
    hotspot.DefaultExports.initialize()
    this
  }

  lazy val dbConfig: DBConfig    = config.db
  lazy val apiConfig: HttpConfig = config.api
  
  // JWT secret from configuration or default
  override lazy val jwtSecret: String = "your-secret-key-change-in-production"

  lazy val metricsApi: MetricsApi = wire[MetricsApi]
  lazy val httpApi: HttpApi       = wire[HttpApi]
}

object MainModule {
  lazy val initialize: MainModule = new MainModule {}.initialize()

  val resource: Resource[IO, MainModule] = Resource.make(IO(initialize))(_ => IO.unit)
}
