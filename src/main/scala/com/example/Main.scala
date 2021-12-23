package com.example

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging

object Main extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    val module = MainModule.initialize
    module.httpApi.resource
      .use { _ =>
        val baseUrl = module.httpConfig.toUrl("http://")
        logger.info(s"Documentation from $baseUrl/docs")
        logger.info(s"Metrics from $baseUrl/metrics")
        IO.never
      }
      .as(ExitCode.Success)
  }

}
