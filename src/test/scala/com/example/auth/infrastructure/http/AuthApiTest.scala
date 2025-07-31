package com.example.auth.infrastructure.http

import cats.effect.IO
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest, UserLoginResponse}
import com.example.auth.infrastructure.codecs.AuthCodecs
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.headers.Authorization

class AuthApiTest extends HasHttp4sRoutesSuite with AuthCodecs {

  override val routes: HttpRoutes[IO] = module.authApi.routes

  test("POST /auth/register should create a new user") {
    val request = Request[IO](Method.POST, uri"/auth/register")
      .withEntity(UserCreateRequest("testuser", "test@example.com", "password123"))
    
    for {
      response     <- routes.orNotFound(request)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Created)
      assertEquals(responseBody.user.username, "testuser")
      assertEquals(responseBody.user.email, "test@example.com")
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/register should fail with duplicate username") {
    val registerRequest = UserCreateRequest("duplicateuser", "duplicate@example.com", "password123")
    val request1 = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val request2 = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    
    for {
      _         <- routes.orNotFound(request1)
      response2 <- routes.orNotFound(request2)
    } yield {
      assertEquals(response2.status, Status.Conflict)
    }
  }

  test("POST /auth/login should authenticate user") {
    val registerRequest = UserCreateRequest("loginuser", "login@example.com", "password123")
    val regReq = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val loginRequest = UserLoginRequest("loginuser", "password123")
    val loginReq = Request[IO](Method.POST, uri"/auth/login").withEntity(loginRequest)
    
    for {
      _            <- routes.orNotFound(regReq)
      response     <- routes.orNotFound(loginReq)
      responseBody <- response.as[UserLoginResponse]
    } yield {
      assertEquals(response.status, Status.Ok)
      assertEquals(responseBody.user.username, "loginuser")
      assert(responseBody.token.nonEmpty)
    }
  }

  test("POST /auth/login should fail with wrong credentials") {
    val registerRequest = UserCreateRequest("wronguser", "wrong@example.com", "password123")
    val regReq = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    val wrongLoginRequest = UserLoginRequest("wronguser", "wrongpassword")
    val wrongReq = Request[IO](Method.POST, uri"/auth/login").withEntity(wrongLoginRequest)
    
    for {
      _        <- routes.orNotFound(regReq)
      response <- routes.orNotFound(wrongReq)
    } yield {
      assertEquals(response.status, Status.Unauthorized)
    }
  }

  test("GET /auth/validate should validate token") {
    val registerRequest = UserCreateRequest("validateuser", "validate@example.com", "password123")
    val regReq = Request[IO](Method.POST, uri"/auth/register").withEntity(registerRequest)
    
    for {
      regResponse  <- routes.orNotFound(regReq)
      regBody      <- regResponse.as[UserLoginResponse]
      validateReq   = Request[IO](Method.GET, uri"/auth/validate")
                        .withHeaders(Authorization(Credentials.Token(AuthScheme.Bearer, regBody.token)))
      response     <- routes.orNotFound(validateReq)
      responseBody <- response.as[String]
    } yield {
      assertEquals(response.status, Status.Ok)
      assert(responseBody.contains("Token is valid for user: validateuser"))
    }
  }
}
