package com.example.auth

import com.example.auth.application.{AuthService, JwtService, PasswordService}
import com.example.auth.domain.UserRepository
import com.example.auth.infrastructure.http.AuthApi
import com.example.auth.infrastructure.repository.SlickUserRepository
import com.example.auth.infrastructure.service.{AuthServiceImpl, JwtServiceImpl, PasswordServiceImpl}
import com.softwaremill.macwire._

trait AuthModule {
  // JWT secret from configuration or default
  private lazy val jwtSecret: String = "your-secret-key-change-in-production"
  
  lazy val userRepository: UserRepository = wire[SlickUserRepository]
  lazy val passwordService: PasswordService = PasswordServiceImpl()
  lazy val jwtService: JwtService = JwtServiceImpl(jwtSecret)
  lazy val authService: AuthService = wire[AuthServiceImpl]
  lazy val authApi: AuthApi = wire[AuthApi]
}

object AuthModule {
  def apply(): AuthModule = new AuthModule {}
}