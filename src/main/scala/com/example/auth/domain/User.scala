package com.example.auth.domain

import com.example.shared.domain.common.Id

import java.time.Instant
import java.util.UUID

final case class User(
    id: Id,
    username: String,
    email: String,
    passwordHash: String,
    createdAt: Instant,
    updatedAt: Instant
)

object User {
  def create(username: String, email: String, passwordHash: String): User = {
    val now = Instant.now()
    User(
      id = Id(UUID.randomUUID()),
      username = username,
      email = email,
      passwordHash = passwordHash,
      createdAt = now,
      updatedAt = now
    )
  }
}

final case class UserCreateRequest(
    username: String,
    email: String,
    password: String
)

final case class UserLoginRequest(
    username: String,
    password: String
)

final case class UserLoginResponse(
    token: String,
    user: UserInfo
)

final case class UserInfo(
    id: Id,
    username: String,
    email: String,
    createdAt: Instant
)

object UserInfo {
  def fromUser(user: User): UserInfo = UserInfo(
    id = user.id,
    username = user.username,
    email = user.email,
    createdAt = user.createdAt
  )
}