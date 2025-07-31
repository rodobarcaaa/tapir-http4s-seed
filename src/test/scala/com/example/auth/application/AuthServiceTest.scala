package com.example.auth.application

import cats.effect.IO
import com.example.MainModule
import com.example.auth.domain.{UserCreateRequest, UserLoginRequest}
import com.example.global.infrastructure.slick.Fly4sModule
import com.example.shared.domain.shared.AlphaNumericMother
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class AuthServiceTest extends CatsEffectSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Fly4sModule.migrateDbResource.use(_ => IO.unit).unsafeRunSync()
  }

  lazy val module: MainModule  = MainModule.initialize
  private lazy val authService = module.authService

  test("register should create a new user successfully") {
    val uniqueUsername = s"testuser-${AlphaNumericMother.random(8)}"
    val uniqueEmail    = s"test-${AlphaNumericMother.random(8)}@example.com"
    val request        = UserCreateRequest(uniqueUsername, uniqueEmail, "password123")
    for {
      response <- authService.register(request)
    } yield {
      assertEquals(response.user.username, uniqueUsername)
      assertEquals(response.user.email, uniqueEmail)
      assert(response.token.nonEmpty)
    }
  }

  test("register should fail with existing username") {
    val uniqueUsername   = s"testuser-${AlphaNumericMother.random(8)}"
    val request          = UserCreateRequest(uniqueUsername, s"test-${AlphaNumericMother.random(8)}@example.com", "password123")
    val duplicateRequest =
      UserCreateRequest(uniqueUsername, s"different-${AlphaNumericMother.random(8)}@example.com", "password123")
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
    val uniqueUsername  = s"testuser-${AlphaNumericMother.random(8)}"
    val registerRequest =
      UserCreateRequest(uniqueUsername, s"test-${AlphaNumericMother.random(8)}@example.com", "password123")
    val loginRequest    = UserLoginRequest(uniqueUsername, "password123")
    for {
      _        <- authService.register(registerRequest)
      response <- authService.login(loginRequest)
    } yield {
      assertEquals(response.user.username, uniqueUsername)
      assert(response.token.nonEmpty)
    }
  }

  test("login should fail with incorrect password") {
    val uniqueUsername  = s"testuser-${AlphaNumericMother.random(8)}"
    val registerRequest =
      UserCreateRequest(uniqueUsername, s"test-${AlphaNumericMother.random(8)}@example.com", "password123")
    val loginRequest    = UserLoginRequest(uniqueUsername, "wrongpassword")
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
    val uniqueUsername  = s"testuser-${AlphaNumericMother.random(8)}"
    val registerRequest =
      UserCreateRequest(uniqueUsername, s"test-${AlphaNumericMother.random(8)}@example.com", "password123")
    for {
      loginResponse    <- authService.register(registerRequest)
      validationResult <- authService.validateToken(loginResponse.token)
    } yield {
      assert(validationResult.isDefined)
      validationResult.foreach { authUser =>
        assertEquals(authUser.user.username, uniqueUsername)
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
