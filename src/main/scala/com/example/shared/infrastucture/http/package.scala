package com.example.shared.infrastucture

import cats.effect.IO
import org.http4s.HttpRoutes
import sttp.tapir.Endpoint
import sttp.tapir.server.ServerEndpoint.Full

package object http {

  type ServerEndpoints = List[Full[_, _, _, _, _, Any, IO]]
  type ServerDocs      = List[Endpoint[_, _, _, _, Any]]
  type ServerRoutes    = HttpRoutes[IO]

}
