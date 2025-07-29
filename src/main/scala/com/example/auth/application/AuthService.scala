package com.example.auth.application

import cats.effect.IO
import com.example.auth.domain._
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.Fail

import java.util.UUID

class AuthService {

  // Simple in-memory user store for demo purposes
  private val users = Map(
    "admin" -> User(
      id = Id(UUID.fromString("00000000-0000-0000-0000-000000000001")),
      username = "admin",
      email = "admin@example.com",
      passwordHash = "password" // In real app, this would be hashed
    ),
    "user" -> User(
      id = Id(UUID.fromString("00000000-0000-0000-0000-000000000002")),
      username = "user",
      email = "user@example.com",
      passwordHash = "password"
    )
  )

  // Simple token store for demo purposes
  private var tokens = Map.empty[String, AuthToken]

  def login(request: LoginRequest): IO[LoginResponse] = {
    users.get(request.username) match {
      case Some(user) if user.passwordHash == request.password =>
        val token = UUID.randomUUID().toString
        val authToken = AuthToken(token, user.id)
        tokens = tokens + (token -> authToken)
        
        IO.pure(LoginResponse(
          token = token,
          user = UserInfo(user.id, user.username, user.email)
        ))
        
      case _ =>
        IO.raiseError(Fail.Unauthorized("Invalid username or password"))
    }
  }

  def validateToken(token: String): IO[User] = {
    tokens.get(token) match {
      case Some(authToken) =>
        users.values.find(_.id == authToken.userId) match {
          case Some(user) => IO.pure(user)
          case None => IO.raiseError(Fail.Unauthorized("Invalid token"))
        }
      case None =>
        IO.raiseError(Fail.Unauthorized("Invalid token"))
    }
  }
}