package com.example.auth.application

import cats.effect.IO
import com.example.MainModule
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest}
import com.example.global.infrastructure.slick.Fly4sModule
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class AuthServiceTest extends CatsEffectSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Fly4sModule.migrateDbResource.use(_ => IO.unit).unsafeRunSync()
  }

  lazy val module: MainModule = MainModule.initialize
  private lazy val authService = module.authService

  test("register should create a new user successfully") {
    val request = UserCreateRequest("testuser1", "test1@example.com", "password123")
    for {
      response <- authService.register(request)
    } yield {
      assertEquals(response.user.username, "testuser1")
      assertEquals(response.user.email, "test1@example.com")
      assert(response.token.nonEmpty)
    }
  }

  test("register should fail with existing username") {
    val request = UserCreateRequest("testuser2", "test2@example.com", "password123")
    val duplicateRequest = UserCreateRequest("testuser2", "different2@example.com", "password123")
    for {
      _      <- authService.register(request)
      result <- authService.register(duplicateRequest).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.Conflict])
      }
    }
  }

  test("login should succeed with correct credentials") {
    val registerRequest = UserCreateRequest("testuser3", "test3@example.com", "password123")
    val loginRequest = UserLoginRequest("testuser3", "password123")
    for {
      _        <- authService.register(registerRequest)
      response <- authService.login(loginRequest)
    } yield {
      assertEquals(response.user.username, "testuser3")
      assert(response.token.nonEmpty)
    }
  }

  test("login should fail with incorrect password") {
    val registerRequest = UserCreateRequest("testuser4", "test4@example.com", "password123")
    val loginRequest = UserLoginRequest("testuser4", "wrongpassword")
    for {
      _      <- authService.register(registerRequest)
      result <- authService.login(loginRequest).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.Unauthorized])
      }
    }
  }

  test("validateToken should validate a valid token") {
    val registerRequest = UserCreateRequest("testuser5", "test5@example.com", "password123")
    for {
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