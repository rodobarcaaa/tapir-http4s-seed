package com.example.auth.application

import com.example.auth.domain.{UserCreateRequest, UserLoginRequest}
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}

class AuthServiceTest extends HasHttp4sRoutesSuite {

  private lazy val authService = module.authService

  test("register should create a new user successfully") {
    for {
      request         = UserCreateRequest("testuser1", "test1@example.com", "password123")
      response       <- authService.register(request)
    } yield {
      assertEquals(response.user.username, "testuser1")
      assertEquals(response.user.email, "test1@example.com")
      assert(response.token.nonEmpty)
    }
  }

  test("register should fail with existing username") {
    for {
      request          = UserCreateRequest("testuser2", "test2@example.com", "password123")
      _               <- authService.register(request)
      duplicateRequest = UserCreateRequest("testuser2", "different2@example.com", "password123")
      result          <- authService.register(duplicateRequest).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.Conflict])
      }
    }
  }

  test("login should succeed with correct credentials") {
    for {
      registerRequest = UserCreateRequest("testuser3", "test3@example.com", "password123")
      _              <- authService.register(registerRequest)
      loginRequest    = UserLoginRequest("testuser3", "password123")
      response       <- authService.login(loginRequest)
    } yield {
      assertEquals(response.user.username, "testuser3")
      assert(response.token.nonEmpty)
    }
  }

  test("login should fail with incorrect password") {
    for {
      registerRequest = UserCreateRequest("testuser4", "test4@example.com", "password123")
      _              <- authService.register(registerRequest)
      loginRequest    = UserLoginRequest("testuser4", "wrongpassword")
      result         <- authService.login(loginRequest).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.Unauthorized])
      }
    }
  }

  test("validateToken should validate a valid token") {
    for {
      registerRequest   = UserCreateRequest("testuser5", "test5@example.com", "password123")
      loginResponse    <- authService.register(registerRequest)
      validationResult <- authService.validateToken(loginResponse.token)
    } yield {
      assert(validationResult.isDefined)
      validationResult.foreach { authUser =>
        assertEquals(authUser.user.username, "testuser5")
      }
    }
  }

  test("validateToken should reject an invalid token") {
    for {
      validationResult <- authService.validateToken("invalid-token")
    } yield {
      assert(validationResult.isEmpty)
    }
  }
}