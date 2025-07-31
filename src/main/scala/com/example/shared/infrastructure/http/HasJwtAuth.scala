package com.example.shared.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain.AuthenticatedUser

trait HasJwtAuth extends HasTapirResource {

  def authService: AuthService

  // JWT Authentication input
  val jwtAuth: EndpointInput[String] = auth.bearer[String]()

  // Helper to validate JWT token and extract authenticated user
  def validateJwtToken(token: String): IO[Either[Fail, AuthenticatedUser]] = {
    authService.validateToken(token).map {
      case Some(authUser) => Right(authUser)
      case None           => Left(Fail.Unauthorized("Invalid or expired token"))
    }
  }
}
