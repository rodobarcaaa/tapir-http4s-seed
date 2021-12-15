package com.example

import cats.effect.{ExitCode, IO, IOApp}
import com.example.global.infrastructure.config.Config
import com.typesafe.scalalogging.StrictLogging

object Main extends IOApp with StrictLogging {

  override def run(args: List[String]): IO[ExitCode] = {
    val initModule = new InitModule {}
    initModule.initialize()
    val mainModule = new MainModule { override def config: Config = initModule.config }
    mainModule.httpApi.resource.use(_ => IO.never).as(ExitCode.Success)
  }

}
