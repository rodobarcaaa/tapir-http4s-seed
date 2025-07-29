package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain._
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http.Fail
import io.circe.generic.auto._
import munit.Http4sHttpRoutesSuite
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

class AuthApiTest extends Http4sHttpRoutesSuite with AuthCodecs {

  private val authService = new AuthService()
  private val authApi = new AuthApi(authService)
  override val routes: HttpRoutes[IO] = authApi.routes

  test(POST(LoginRequest("admin", "password"), uri"auth" / "login")).alias("LOGIN SUCCESS") { response =>
    assertEquals(response.status, Status.Ok)
    assertIOBoolean(response.as[LoginResponse].map(_.user.username == "admin"))
  }

  test(POST(LoginRequest("admin", "wrongpassword"), uri"auth" / "login")).alias("LOGIN INVALID PASSWORD") { response =>
    assertEquals(response.status, Status.Unauthorized)
    assertIO(response.as[Fail.Unauthorized], Fail.Unauthorized("Invalid username or password"))
  }

  test(POST(LoginRequest("nonexistent", "password"), uri"auth" / "login")).alias("LOGIN INVALID USER") { response =>
    assertEquals(response.status, Status.Unauthorized)
    assertIO(response.as[Fail.Unauthorized], Fail.Unauthorized("Invalid username or password"))
  }

  test(POST(LoginRequest("user", "password"), uri"auth" / "login")).alias("LOGIN USER SUCCESS") { response =>
    assertEquals(response.status, Status.Ok)
    assertIOBoolean(response.as[LoginResponse].map(_.user.username == "user"))
  }
}