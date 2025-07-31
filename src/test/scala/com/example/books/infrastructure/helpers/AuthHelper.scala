package com.example.books.infrastructure.helpers

import com.example.auth.domain.{UserCreateRequest, UserLoginResponse}
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Header}

trait AuthHelper {
  self: HasHttp4sRoutesSuite =>

  private lazy val authService = module.authService

  // Create a test user and return JWT token
  def createTestUserAndGetToken(username: String = "testuser", email: String = "test@example.com", password: String = "password123"): String = {
    val createRequest = UserCreateRequest(username, email, password)
    val response = authService.register(createRequest).unsafeRunSync()
    response.token
  }

  // Get JWT auth header
  def jwtAuthHeader(token: String): Header.Raw = 
    Authorization(Credentials.Token(AuthScheme.Bearer, token)).toRaw1

  // Create a default test token
  lazy val defaultTestToken: String = createTestUserAndGetToken()
  lazy val defaultAuthHeader: Header.Raw = jwtAuthHeader(defaultTestToken)
}