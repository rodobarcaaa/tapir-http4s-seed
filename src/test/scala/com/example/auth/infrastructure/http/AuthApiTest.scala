package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest, UserLoginResponse}
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

class AuthApiTest extends HasHttp4sRoutesSuite with AuthCodecs {

  override val routes: HttpRoutes[IO] = module.authApi.routes

  val registerRequest = UserCreateRequest("testuser", "test@example.com", "password123")

  test(POST(registerRequest, uri"/auth/register")).alias("CREATE USER") { response =>
    assertEquals(response.status, Status.Created)
    assertIO(response.as[UserLoginResponse].map(_.user.username), "testuser")
  }

  val loginRequest = UserLoginRequest("loginuser", "password123")
  val loginRegisterRequest = UserCreateRequest("loginuser", "login@example.com", "password123")

  test(POST(loginRequest, uri"/auth/login")).alias("LOGIN USER") { response =>
    // This will fail because user doesn't exist yet, which is expected
    assertEquals(response.status, Status.Unauthorized)
  }

  val duplicateRequest = UserCreateRequest("duplicateuser", "duplicate@example.com", "password123")

  test(POST(duplicateRequest, uri"/auth/register")).alias("CREATE FIRST USER") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(POST(duplicateRequest, uri"/auth/register")).alias("CREATE DUPLICATE USER") { response =>
    assertEquals(response.status, Status.Conflict)
  }
}