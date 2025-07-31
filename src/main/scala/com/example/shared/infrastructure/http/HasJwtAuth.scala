package com.example.shared.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain.{AuthenticatedUser, RoleAuthorization}

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

  // Authorization helpers to eliminate code duplication
  def withAdminAuth[T](token: String)(operation: => IO[Either[Fail, T]]): IO[Either[Fail, T]] = {
    validateJwtToken(token).flatMap {
      case Right(authUser) =>
        RoleAuthorization.requireAdmin(authUser.user) match {
          case Right(_)    => operation
          case Left(error) => IO.pure(Left(error))
        }
      case Left(error)     => IO.pure(Left(error))
    }
  }

  def withAuth[T](token: String)(operation: => IO[Either[Fail, T]]): IO[Either[Fail, T]] = {
    validateJwtToken(token).flatMap {
      case Right(_)    => operation
      case Left(error) => IO.pure(Left(error))
    }
  }
}
