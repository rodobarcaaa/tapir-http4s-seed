package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest, UserLoginResponse}
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.domain.shared.AlphaNumericMother
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.headers.Authorization

class AuthApiTest extends HasHttp4sRoutesSuite with AuthCodecs {

  override val routes: HttpRoutes[IO] = module.authApi.routes

  test("POST /auth/register should create a new user") {
    val uniqueUsername = s"testuser-${AlphaNumericMother.random(8)}"
    val uniqueEmail = s"test-${AlphaNumericMother.random(8)}@example.com"
    val request = Request[IO](Method.POST, uri"/auth/register")
      .withEntity(UserCreateRequest(uniqueUsername, uniqueEmail, "password123"))

    for {
      response     <- routes.orNotFound(request)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Created)
      assertEquals(responseBody.user.username, uniqueUsername)
      assertEquals(responseBody.user.email, uniqueEmail)
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/register should fail with duplicate username") {
    val uniqueUsername = s"duplicateuser-${AlphaNumericMother.random(8)}"
    val registerRequest = UserCreateRequest(uniqueUsername, s"duplicate-${AlphaNumericMother.random(8)}@example.com", "password123")
    val request1        = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val request2        = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)

    for {
      _         <- routes.orNotFound(request1)
      response2 <- routes.orNotFound(request2)
    } yield {
      assertEquals(response2.status, Status.Conflict)
    }
  }

  test("POST /auth/login should authenticate user") {
    val uniqueUsername = s"loginuser-${AlphaNumericMother.random(8)}"
    val registerRequest = UserCreateRequest(uniqueUsername, s"login-${AlphaNumericMother.random(8)}@example.com", "password123")
    val regReq          = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val loginRequest    = UserLoginRequest(uniqueUsername, "password123")
    val loginReq        = Request[IO](Method.POST, uri"/auth/login").withEntity(loginRequest)

    for {
      _            <- routes.orNotFound(regReq)
      response     <- routes.orNotFound(loginReq)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Ok)
      assertEquals(responseBody.user.username, uniqueUsername)
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/login should fail with wrong credentials") {
    val uniqueUsername = s"wronguser-${AlphaNumericMother.random(8)}"
    val registerRequest   = UserCreateRequest(uniqueUsername, s"wrong-${AlphaNumericMother.random(8)}@example.com", "password123")
    val regReq            = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val wrongLoginRequest = UserLoginRequest(uniqueUsername, "wrongpassword")
    val wrongReq          = Request[IO](Method.POST, uri"/auth/login").withEntity(wrongLoginRequest)

    for {
      _        <- routes.orNotFound(regReq)
      response <- routes.orNotFound(wrongReq)
    } yield {
      assertEquals(response.status, Status.Unauthorized)
    }
  }

  test("GET /auth/validate should validate token") {
    val uniqueUsername = s"validateuser-${AlphaNumericMother.random(8)}"
    val registerRequest = UserCreateRequest(uniqueUsername, s"validate-${AlphaNumericMother.random(8)}@example.com", "password123")
    val regReq          = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)

    for {
      regResponse  <- routes.orNotFound(regReq)
      regBody      <- regResponse.as[UserLoginResponse]
      validateReq   = Request[IO](Method.GET, uri"/auth/validate")
                        .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, regBody.token)))
      response     <- routes.orNotFound(validateReq)
      responseBody <- response.as[String]
    } yield {
      assertEquals(response.status, Status.Ok)
      assert(responseBody.contains(s"Token is valid for user: $uniqueUsername"))
    }
  }
}
