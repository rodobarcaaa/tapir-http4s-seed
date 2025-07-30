package com.example.auth.application

import cats.effect.IO
import com.example.auth.domain.{AuthenticatedUser, User, UserInfo}

trait JwtRepository {
  def generateToken(user: User): IO[String]
  def validateToken(token: String): IO[Option[AuthenticatedUser]]
  def getUserFromToken(token: String): IO[Option[UserInfo]]
}