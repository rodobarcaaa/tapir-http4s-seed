package com.example.auth.infrastructure.http

import com.example.auth.application.AuthService
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http._
import cats.effect.IO

class SecuredExampleApi(service: AuthService) extends HasTapirResource with AuthCodecs with HasTokenAuth {

  // Use the authService from HasTokenAuth trait
  override def authService: AuthService = service

  // Example secured endpoint that requires x-token
  private val securedExample = securedEndpoint
    .get
    .in("secured")
    .in("example")
    .out(jsonBody[Map[String, String]])
    .serverLogic { _ => 
      IO.pure(Map("message" -> "This is a secured endpoint", "status" -> "success")).orError
    }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(securedExample)
}