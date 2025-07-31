package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest, UserLoginResponse}
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.auth.infrastructure.repository.{InMemoryUserRepository, SlickJwtRepository, SlickPasswordRepository}
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.headers.Authorization
import org.http4s.implicits._

class AuthApiTest extends CatsEffectSuite with AuthCodecs {

  val jwtSecret = "test-secret-key"

  def createAuthApi(): IO[AuthApi] = {
    for {
      userRepository    <- InMemoryUserRepository.create()
      passwordRepository = SlickPasswordRepository()
      jwtRepository      = SlickJwtRepository(jwtSecret)
      authService        = new AuthService(userRepository, passwordRepository, jwtRepository)
    } yield new AuthApi(authService)
  }

  test("POST /auth/register should create a new user") {
    for {
      authApi      <- createAuthApi()
      request       = Request[IO](Method.POST, uri"/auth/register")
                        .withEntity(UserCreateRequest("testuser", "test@example.com", "password123"))
      response     <- authApi.routes.orNotFound(request)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Created)
      assertEquals(responseBody.user.username, "testuser")
      assertEquals(responseBody.user.email, "test@example.com")
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/register should fail with duplicate username") {
    for {
      authApi        <- createAuthApi()
      registerRequest = UserCreateRequest("duplicateuser", "duplicate@example.com", "password123")
      request1        = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
      _              <- authApi.routes.orNotFound(request1)
      // Try to register same user again
      request2        = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
      response2      <- authApi.routes.orNotFound(request2)
    } yield {
      assertEquals(response2.status, Status.Conflict)
    }
  }

  test("POST /auth/login should authenticate user") {
    for {
      authApi        <- createAuthApi()
      registerRequest = UserCreateRequest("loginuser", "login@example.com", "password123")
      regReq          = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
      _              <- authApi.routes.orNotFound(regReq)

      loginRequest  = UserLoginRequest("loginuser", "password123")
      loginReq      = Request[IO](Method.POST, uri"/auth/login").withEntity(loginRequest)
      response     <- authApi.routes.orNotFound(loginReq)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Ok)
      assertEquals(responseBody.user.username, "loginuser")
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/login should fail with wrong credentials") {
    for {
      authApi        <- createAuthApi()
      registerRequest = UserCreateRequest("wronguser", "wrong@example.com", "password123")
      regReq          = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
      _              <- authApi.routes.orNotFound(regReq)

      wrongLoginRequest = UserLoginRequest("wronguser", "wrongpassword")
      wrongReq          = Request[IO](Method.POST, uri"/auth/login").withEntity(wrongLoginRequest)
      response         <- authApi.routes.orNotFound(wrongReq)
    } yield {
      assertEquals(response.status, Status.Unauthorized)
    }
  }

  test("GET /auth/validate should validate token") {
    for {
      authApi        <- createAuthApi()
      registerRequest = UserCreateRequest("validateuser", "validate@example.com", "password123")
      regReq          = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
      regResponse    <- authApi.routes.orNotFound(regReq)
      regBody        <- regResponse.as[UserLoginResponse]

      validateReq   = Request[IO](Method.GET, uri"/auth/validate")
                        .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, regBody.token)))
      response     <- authApi.routes.orNotFound(validateReq)
      responseBody <- response.as[String]
    } yield {
      assertEquals(response.status, Status.Ok)
      assert(responseBody.contains("Token is valid for user: validateuser"))
    }
  }
}
