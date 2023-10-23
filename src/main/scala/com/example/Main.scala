package com.example

import cats.effect.{ExitCode, IO, IOApp}
import com.example.global.infrastructure.slick.Fly4sMigrations
import com.typesafe.scalalogging.StrictLogging

object Main extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    val module = MainModule.initialize

    val migrateDb = Fly4sMigrations.migrateDb(module.dbConfig).use(_ => IO.unit)

    val startHttpApi = module.httpApi.resource
      .use { _ =>
        val baseUrl = module.httpConfig.toUrl("http://")
        logger.info(s"Documentation from $baseUrl/docs")
        logger.info(s"Metrics from $baseUrl/metrics")
        IO.never
      }

    migrateDb *> startHttpApi.as(ExitCode.Success)
  }

}
