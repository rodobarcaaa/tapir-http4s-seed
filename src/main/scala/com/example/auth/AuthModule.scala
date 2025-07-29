package com.example.auth

import com.example.auth.application.AuthService
import com.example.auth.infrastructure.http.{AuthApi, SecuredExampleApi}
import com.softwaremill.macwire._

trait AuthModule {
  lazy val authService: AuthService = wire[AuthService]
  lazy val authApi: AuthApi = wire[AuthApi]
  lazy val securedExampleApi: SecuredExampleApi = wire[SecuredExampleApi]
}