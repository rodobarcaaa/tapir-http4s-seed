package com.example.auth.domain

import com.example.shared.domain.common.Id

import java.time.Instant
import java.util.UUID

final case class AuthToken(
    id: Id,
    userId: Id,
    token: String,
    expiresAt: Instant,
    createdAt: Instant
)

object AuthToken {
  def create(userId: Id, token: String, expiresAt: Instant): AuthToken = {
    AuthToken(
      id = Id(UUID.randomUUID()),
      userId = userId,
      token = token,
      expiresAt = expiresAt,
      createdAt = Instant.now()
    )
  }
}

final case class AuthenticatedUser(
    user: UserInfo,
    token: String
)
