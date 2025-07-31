package com.example.shared.infrastructure

import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint.Full

package object http {
  type ServerEndpoints = List[Full[?, ?, ?, ?, ?, Any, IO]]
  type ServerDocs      = List[Endpoint[?, ?, ?, ?, Any]]
  type ServerRoutes    = HttpRoutes[IO]
}
