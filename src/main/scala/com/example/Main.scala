package com.example

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.scalalogging.StrictLogging

object Main extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] =
    MainModule.initialize.httpApi.resource
      .use(_ => IO.never)
      .as(ExitCode.Success)

}
