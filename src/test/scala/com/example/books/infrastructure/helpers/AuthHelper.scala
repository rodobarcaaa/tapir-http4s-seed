package com.example.books.infrastructure.helpers

import com.example.auth.domain.{Role, UserCreateRequest, UserLoginRequest}
import com.example.shared.domain.shared.AlphaNumericMother
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import org.http4s.headers.Authorization
import org.http4s.{AuthScheme, Credentials, Header}

trait AuthHelper {
  self: HasHttp4sRoutesSuite =>

  private lazy val authService = module.authService

  // Global singleton test user - created once for all tests (Customer role)
  private lazy val globalTestUsername = s"global-testuser-${AlphaNumericMother.random(12)}"
  private lazy val globalTestEmail    = s"global-test-${AlphaNumericMother.random(12)}@example.com"
  private val globalTestPassword      = "password123"

  // Global singleton admin user - created once for all tests (Admin role)
  private lazy val globalAdminUsername = s"global-admin-${AlphaNumericMother.random(12)}"
  private lazy val globalAdminEmail    = s"global-admin-${AlphaNumericMother.random(12)}@example.com"
  private val globalAdminPassword      = "password123"

  // Synchronized method to ensure only one thread creates the test user
  private def getOrCreateGlobalTestToken(): String = synchronized {
    try {
      val createRequest = UserCreateRequest(globalTestUsername, globalTestEmail, globalTestPassword)
      val response      = authService.register(createRequest).unsafeRunSync()
      response.token
    } catch {
      // If user already exists (conflict), try to login instead
      case _: com.example.shared.infrastructure.http.Fail.Conflict =>
        val loginRequest = UserLoginRequest(globalTestUsername, globalTestPassword)
        val response     = authService.login(loginRequest).unsafeRunSync()
        response.token
    }
  }

  // Synchronized method to ensure only one thread creates the admin user
  private def getOrCreateGlobalAdminToken(): String = synchronized {
    try {
      val createRequest =
        UserCreateRequest(globalAdminUsername, globalAdminEmail, globalAdminPassword, Some(Role.Admin))
      val response      = authService.register(createRequest).unsafeRunSync()
      response.token
    } catch {
      // If user already exists (conflict), try to login instead
      case _: com.example.shared.infrastructure.http.Fail.Conflict =>
        val loginRequest = UserLoginRequest(globalAdminUsername, globalAdminPassword)
        val response     = authService.login(loginRequest).unsafeRunSync()
        response.token
    }
  }

  // Create a test user and return JWT token (for specific test cases)
  def createTestUserAndGetToken(
      username: String = s"testuser-${AlphaNumericMother.random(8)}",
      email: String = s"test-${AlphaNumericMother.random(8)}@example.com",
      password: String = "password123"
  ): String = {
    val createRequest = UserCreateRequest(username, email, password)
    val response      = authService.register(createRequest).unsafeRunSync()
    response.token
  }

  // Get JWT auth header
  def jwtAuthHeader(token: String): Header.Raw =
    Authorization(Credentials.Token(AuthScheme.Bearer, token)).toRaw1

  // Use the global singleton test tokens to avoid conflicts
  lazy val defaultTestToken: String      = getOrCreateGlobalTestToken()
  lazy val defaultAuthHeader: Header.Raw = jwtAuthHeader(defaultTestToken)

  // Admin tokens for operations that require admin privileges
  lazy val adminTestToken: String      = getOrCreateGlobalAdminToken()
  lazy val adminAuthHeader: Header.Raw = jwtAuthHeader(adminTestToken)
}
