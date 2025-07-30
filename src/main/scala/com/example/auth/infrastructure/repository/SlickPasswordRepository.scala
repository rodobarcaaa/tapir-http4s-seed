package com.example.auth.infrastructure.repository

import cats.effect.IO
import com.example.auth.application.PasswordRepository
import org.mindrot.jbcrypt.BCrypt

class SlickPasswordRepository extends PasswordRepository {

  private val saltRounds = 12

  override def hashPassword(password: String): IO[String] = {
    IO.blocking {
      BCrypt.hashpw(password, BCrypt.gensalt(saltRounds))
    }
  }

  override def verifyPassword(password: String, hash: String): IO[Boolean] = {
    IO.blocking {
      BCrypt.checkpw(password, hash)
    }
  }
}

object SlickPasswordRepository {
  def apply(): SlickPasswordRepository = new SlickPasswordRepository()
}