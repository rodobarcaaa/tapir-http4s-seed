package com.example.auth.infrastructure.slick

import com.example.auth.domain.User
import com.example.shared.infrastructure.slick.HasSlickPgProvider

import java.time.Instant
import java.util.UUID

trait UserMapping {
  self: HasSlickPgProvider =>

  import profile.api._

  final class UserTable(tag: Tag) extends Table[User](tag, "users") {

    def id           = column[UUID]("id", O.PrimaryKey)
    def username     = column[String]("username", O.Length(255), O.Unique)
    def email        = column[String]("email", O.Length(255), O.Unique)
    def passwordHash = column[String]("password_hash")
    def createdAt    = column[Instant]("created_at")
    def updatedAt    = column[Instant]("updated_at")

    def * = (id, username, email, passwordHash, createdAt, updatedAt) <> (User.tupled, User.unapply)
  }

  val Users = TableQuery[UserTable]
}