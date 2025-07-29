package com.example.auth.infrastructure.http

import com.example.auth.application.AuthService
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http._
import cats.effect.IO

class SecuredExampleApi(service: AuthService) extends HasTapirResource with AuthCodecs {

  // Define the x-token header input
  val xTokenHeader: EndpointInput[String] = header[String]("x-token")

  // Example secured endpoint that requires x-token manually
  private val securedExample = baseEndpoint
    .get
    .in("secured")
    .in("example")
    .in(xTokenHeader)
    .out(jsonBody[Map[String, String]])
    .serverLogic { token => 
      service.validateToken(token).flatMap { _ =>
        val response = Map("message" -> "This is a secured endpoint", "status" -> "success")
        IO.pure(response)
      }.orError
    }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(securedExample)
}