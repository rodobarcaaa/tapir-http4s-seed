package com.example.auth.infrastructure.http

import com.example.auth.application.AuthService
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest, UserLoginResponse}
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http._

class AuthApi(service: AuthService) extends HasTapirResource with AuthCodecs {

  // Base endpoint for authentication
  private val base = baseEndpoint.tag("Authentication").in("auth")

  // Register a new user
  private val register = base.post
    .in("register")
    .in(jsonBody[UserCreateRequest])
    .out(statusCode(Created))
    .out(jsonBody[UserLoginResponse])
    .serverLogic { request => service.register(request).orError }

  // Login user
  private val login = base.post
    .in("login")
    .in(jsonBody[UserLoginRequest])
    .out(statusCode(Ok))
    .out(jsonBody[UserLoginResponse])
    .serverLogic { request => service.login(request).orError }

  // Validate token endpoint
  private val validate = base.get
    .in("validate")
    .in(auth.bearer[String]())
    .out(statusCode(Ok))
    .out(jsonBody[String])
    .serverLogic { token =>
      service.validateToken(token).map {
        case Some(authUser) => Right(s"Token is valid for user: ${authUser.user.username}")
        case None           => Left(Fail.Unauthorized("Invalid or expired token"): Fail)
      }
    }

  // Endpoints to expose
  override val endpoints: ServerEndpoints = List(register, login, validate)
}
