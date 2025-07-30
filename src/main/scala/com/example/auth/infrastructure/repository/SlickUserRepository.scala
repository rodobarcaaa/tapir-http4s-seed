package com.example.auth.infrastructure.repository

import cats.effect.IO
import com.example.auth.domain.{User, UserRepository}
import com.example.auth.infrastructure.slick.UserMapping
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.slick.HasSlickPgProvider

class SlickUserRepository extends HasSlickPgProvider with UserRepository with UserMapping {

  import profile.api._

  override def save(user: User): IO[User] = {
    val insertAction = Users.insertOrUpdate(user)
    IO.fromFuture(IO(db.run(insertAction))).as(user)
  }

  override def findById(id: Id): IO[Option[User]] = {
    val query = Users.filter(_.id === id.value)
    IO.fromFuture(IO(db.run(query.result.headOption)))
  }

  override def findByUsername(username: String): IO[Option[User]] = {
    val query = Users.filter(_.username === username)
    IO.fromFuture(IO(db.run(query.result.headOption)))
  }

  override def findByEmail(email: String): IO[Option[User]] = {
    val query = Users.filter(_.email === email)
    IO.fromFuture(IO(db.run(query.result.headOption)))
  }
}