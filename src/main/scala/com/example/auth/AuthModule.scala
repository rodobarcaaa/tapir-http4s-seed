package com.example.auth

import com.example.auth.application.{AuthService, JwtRepository, PasswordRepository}
import com.example.auth.domain.UserRepository
import com.example.auth.infrastructure.http.AuthApi
import com.example.auth.infrastructure.repository.{SlickJwtRepository, SlickPasswordRepository, SlickUserRepository}
import com.softwaremill.macwire._

trait AuthModule {
  // JWT secret from configuration or default
  private lazy val jwtSecret: String = "your-secret-key-change-in-production"

  lazy val userRepository: UserRepository         = wire[SlickUserRepository]
  lazy val passwordRepository: PasswordRepository = SlickPasswordRepository()
  lazy val jwtRepository: JwtRepository           = SlickJwtRepository(jwtSecret)
  lazy val authService: AuthService               = wire[AuthService]
  lazy val authApi: AuthApi                       = wire[AuthApi]
}

object AuthModule {
  def apply(): AuthModule = new AuthModule {}
}
