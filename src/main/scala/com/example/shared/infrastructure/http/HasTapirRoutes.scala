package com.example.shared.infrastructure.http

import cats.effect.IO
import sttp.tapir.server.http4s.Http4sServerInterpreter

trait HasTapirRoutes extends HasTapirEndpoints {
  lazy val routes: ServerRoutes = Http4sServerInterpreter[IO]().toRoutes(endpoints)
}
