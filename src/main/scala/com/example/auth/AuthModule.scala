package com.example.auth

import cats.effect.{IO, Ref}
import com.example.auth.application.{AuthService, JwtService, PasswordService}
import com.example.auth.domain.{User, UserRepository}
import com.example.auth.infrastructure.http.AuthApi
import com.example.auth.infrastructure.repository.InMemoryUserRepository
import com.example.auth.infrastructure.service.{AuthServiceImpl, JwtServiceImpl, PasswordServiceImpl}
import com.example.shared.domain.common.Id
import com.softwaremill.macwire._

trait AuthModule {
  // JWT secret from configuration or default
  private lazy val jwtSecret: String = "your-secret-key-change-in-production"
  
  // Create the repository as a lazy val that returns an IO
  private lazy val userRepositoryIO: IO[UserRepository] = 
    Ref.of[IO, Map[Id, User]](Map.empty).map(new InMemoryUserRepository(_))
  
  // For now, use an unsafe operation but properly handle it
  lazy val userRepository: UserRepository = {
    import cats.effect.unsafe.implicits.global
    userRepositoryIO.unsafeRunSync()
  }
  
  lazy val passwordService: PasswordService = PasswordServiceImpl()
  lazy val jwtService: JwtService = JwtServiceImpl(jwtSecret)
  lazy val authService: AuthService = wire[AuthServiceImpl]
  lazy val authApi: AuthApi = wire[AuthApi]
}

object AuthModule {
  def apply(): AuthModule = new AuthModule {}
}