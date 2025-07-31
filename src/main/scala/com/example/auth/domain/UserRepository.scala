package com.example.auth.domain

import cats.effect.IO
import com.example.shared.domain.common.Id

trait UserRepository {
  def save(user: User): IO[User]
  def findById(id: Id): IO[Option[User]]
  def findByUsername(username: String): IO[Option[User]]
  def findByEmail(email: String): IO[Option[User]]
}
