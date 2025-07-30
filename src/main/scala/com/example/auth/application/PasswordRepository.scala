package com.example.auth.application

import cats.effect.IO

trait PasswordRepository {
  def hashPassword(password: String): IO[String]
  def verifyPassword(password: String, hash: String): IO[Boolean]
}