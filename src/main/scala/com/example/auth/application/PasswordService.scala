package com.example.auth.application

import cats.effect.IO
import org.mindrot.jbcrypt.BCrypt

trait PasswordService {
  def hashPassword(password: String): IO[String]
  def verifyPassword(password: String, hash: String): IO[Boolean]
}

class PasswordServiceImpl extends PasswordService {

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

object PasswordService {
  def apply(): PasswordService = new PasswordServiceImpl()
}