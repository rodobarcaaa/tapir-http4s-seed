package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain._
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http.Fail
import munit.Http4sHttpRoutesSuite
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import io.circe.generic.auto._

class SecuredExampleApiTest extends Http4sHttpRoutesSuite with AuthCodecs {

  private val authService = new AuthService()
  private val securedExampleApi = new SecuredExampleApi(authService)
  override val routes: HttpRoutes[IO] = securedExampleApi.routes

  // First get a token by logging in
  private val authApi = new AuthApi(authService)
  private val loginResponse = authApi.routes.orNotFound
    .run(Request[IO](
      method = Method.POST,
      uri = uri"auth/login"
    ).withEntity(LoginRequest("admin", "password")))
    .flatMap(_.as[LoginResponse])
    .unsafeRunSync()

  private val validToken = loginResponse.token

  test(GET(uri"secured" / "example").putHeaders("x-token" -> validToken)).alias("SECURED ENDPOINT WITH VALID TOKEN") { response =>
    assertEquals(response.status, Status.Ok)
    assertIOBoolean(response.as[Map[String, String]].map(_.contains("message")))
  }

  test(GET(uri"secured" / "example").putHeaders("x-token" -> "invalid-token")).alias("SECURED ENDPOINT WITH INVALID TOKEN") { response =>
    assertEquals(response.status, Status.Unauthorized)
    assertIO(response.as[Fail.Unauthorized], Fail.Unauthorized("Invalid token"))
  }

  test(GET(uri"secured" / "example")).alias("SECURED ENDPOINT WITHOUT TOKEN") { response =>
    assertEquals(response.status, Status.BadRequest) // Missing header
  }
}