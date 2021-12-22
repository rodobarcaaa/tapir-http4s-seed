package com.example.shared.infrastucture.http

import cats.effect.IO
import sttp.tapir.server.http4s.Http4sServerInterpreter

trait HasTapirRoutes extends HasTapirEndpoints {
  lazy val routes: ServerRoutes = Http4sServerInterpreter[IO]().toRoutes(endpoints)
}
