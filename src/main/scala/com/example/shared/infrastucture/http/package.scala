package com.example.shared.infrastucture

import cats.data._
import cats.effect.IO
import sttp.tapir.server.ServerEndpoint.Full

package object http {

  type ServerEndpoints = NonEmptyList[Full[_, _, _, _, _, Any, IO]]

}
