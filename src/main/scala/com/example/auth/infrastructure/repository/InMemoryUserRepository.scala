package com.example.auth.infrastructure.repository

import cats.effect.{IO, Ref}
import com.example.auth.application.UserRepository
import com.example.auth.domain.User
import com.example.shared.domain.common.Id

class InMemoryUserRepository(storage: Ref[IO, Map[Id, User]]) extends UserRepository {

  override def save(user: User): IO[User] = {
    storage.update(_ + (user.id -> user)).as(user)
  }

  override def findById(id: Id): IO[Option[User]] = {
    storage.get.map(_.get(id))
  }

  override def findByUsername(username: String): IO[Option[User]] = {
    storage.get.map(_.values.find(_.username == username))
  }

  override def findByEmail(email: String): IO[Option[User]] = {
    storage.get.map(_.values.find(_.email == email))
  }
}

object InMemoryUserRepository {
  def create(): IO[InMemoryUserRepository] = {
    Ref.of[IO, Map[Id, User]](Map.empty).map(new InMemoryUserRepository(_))
  }
}