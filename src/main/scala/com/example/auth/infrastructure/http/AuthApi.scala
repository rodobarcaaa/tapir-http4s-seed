package com.example.auth.infrastructure.http

import com.example.auth.application.AuthService
import com.example.auth.domain._
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http._

class AuthApi(service: AuthService) extends HasTapirResource with AuthCodecs {

  // Init
  private val base = baseEndpoint.tag("Authentication").in("auth")

  // Login endpoint
  private val login = base.post
    .in("login")
    .in(jsonBody[LoginRequest])
    .out(jsonBody[LoginResponse])
    .serverLogic { request => service.login(request).orError }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(login)
}