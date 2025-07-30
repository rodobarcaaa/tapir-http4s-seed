package com.example.auth.application

import com.example.auth.domain.{UserCreateRequest, UserLoginRequest}
import com.example.auth.infrastructure.repository.{InMemoryUserRepository, SlickJwtRepository, SlickPasswordRepository}
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class AuthServiceTest extends CatsEffectSuite {

  val jwtSecret          = "test-secret-key"
  val passwordRepository = SlickPasswordRepository()
  val jwtRepository      = SlickJwtRepository(jwtSecret)

  test("register should create a new user successfully") {
    for {
      userRepository <- InMemoryUserRepository.create()
      authService     = new AuthService(userRepository, passwordRepository, jwtRepository)
      request         = UserCreateRequest("testuser", "test@example.com", "password123")
      response       <- authService.register(request)
    } yield {
      assertEquals(response.user.username, "testuser")
      assertEquals(response.user.email, "test@example.com")
      assert(response.token.nonEmpty)
    }
  }

  test("register should fail with existing username") {
    for {
      userRepository  <- InMemoryUserRepository.create()
      authService      = new AuthService(userRepository, passwordRepository, jwtRepository)
      request          = UserCreateRequest("testuser", "test@example.com", "password123")
      _               <- authService.register(request)
      duplicateRequest = UserCreateRequest("testuser", "different@example.com", "password123")
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
      userRepository <- InMemoryUserRepository.create()
      authService     = new AuthService(userRepository, passwordRepository, jwtRepository)
      registerRequest = UserCreateRequest("testuser", "test@example.com", "password123")
      _              <- authService.register(registerRequest)
      loginRequest    = UserLoginRequest("testuser", "password123")
      response       <- authService.login(loginRequest)
    } yield {
      assertEquals(response.user.username, "testuser")
      assert(response.token.nonEmpty)
    }
  }

  test("login should fail with incorrect password") {
    for {
      userRepository <- InMemoryUserRepository.create()
      authService     = new AuthService(userRepository, passwordRepository, jwtRepository)
      registerRequest = UserCreateRequest("testuser", "test@example.com", "password123")
      _              <- authService.register(registerRequest)
      loginRequest    = UserLoginRequest("testuser", "wrongpassword")
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
      userRepository   <- InMemoryUserRepository.create()
      authService       = new AuthService(userRepository, passwordRepository, jwtRepository)
      registerRequest   = UserCreateRequest("testuser", "test@example.com", "password123")
      loginResponse    <- authService.register(registerRequest)
      validationResult <- authService.validateToken(loginResponse.token)
    } yield {
      assert(validationResult.isDefined)
      validationResult.foreach { authUser =>
        assertEquals(authUser.user.username, "testuser")
      }
    }
  }

  test("validateToken should reject an invalid token") {
    for {
      userRepository   <- InMemoryUserRepository.create()
      authService       = new AuthService(userRepository, passwordRepository, jwtRepository)
      validationResult <- authService.validateToken("invalid-token")
    } yield {
      assert(validationResult.isEmpty)
    }
  }
}
