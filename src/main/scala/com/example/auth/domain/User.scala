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

  // apply, unapply and tupled methods to use by slick table mapping
  def apply: (UUID, String, String, String, Instant, Instant) => User = {
    case (id, username, email, passwordHash, createdAt, updatedAt) =>
      User(
        Id(id),
        username,
        email,
        passwordHash,
        createdAt,
        updatedAt
      )
  }

  def unapply: User => Option[(UUID, String, String, String, Instant, Instant)] = { user =>
    Some(
      (
        user.id.value,
        user.username,
        user.email,
        user.passwordHash,
        user.createdAt,
        user.updatedAt
      )
    )
  }

  def tupled: ((UUID, String, String, String, Instant, Instant)) => User = apply.tupled
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