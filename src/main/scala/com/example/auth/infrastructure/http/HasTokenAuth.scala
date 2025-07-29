package com.example.auth.infrastructure.http

import com.example.auth.application.AuthService
import com.example.shared.infrastructure.http.{Fail, HasTapirResource}

trait HasTokenAuth extends HasTapirResource {
  
  def authService: AuthService

  // Define the x-token header input
  val xTokenHeader: EndpointInput[String] = header[String]("x-token")

  // Secured base endpoint that requires x-token authentication
  val securedEndpoint = baseEndpoint
    .securityIn(xTokenHeader)
    .serverSecurityLogic { token =>
      authService.validateToken(token)
        .map(_ => Right(()))
        .handleError {
          case f: Fail => Left(f)
          case _ => Left(Fail.Unauthorized("Invalid token"))
        }
    }
}