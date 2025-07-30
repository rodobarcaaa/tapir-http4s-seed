package com.example.auth.application

import cats.effect.IO
import com.example.auth.domain.{AuthenticatedUser, UserCreateRequest, UserInfo, UserLoginRequest, UserLoginResponse}
import com.example.shared.domain.common.Id

trait AuthService {
  def register(request: UserCreateRequest): IO[UserLoginResponse]
  def login(request: UserLoginRequest): IO[UserLoginResponse]
  def validateToken(token: String): IO[Option[AuthenticatedUser]]
  def getUserInfo(userId: Id): IO[Option[UserInfo]]
}