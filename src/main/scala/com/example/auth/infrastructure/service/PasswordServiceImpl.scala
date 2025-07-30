package com.example.auth.infrastructure.service

import cats.effect.IO
import com.example.auth.application.PasswordService
import org.mindrot.jbcrypt.BCrypt

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

object PasswordServiceImpl {
  def apply(): PasswordServiceImpl = new PasswordServiceImpl()
}