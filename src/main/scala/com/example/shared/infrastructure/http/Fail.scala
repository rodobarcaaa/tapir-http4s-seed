package com.example.shared.infrastructure.http

abstract class Fail extends Exception

object Fail {
  case class NotFound(msg: String) extends Fail

  case class Conflict(msg: String) extends Fail

  case class IncorrectInput(msg: String) extends Fail

  case class Unauthorized(msg: String) extends Fail

  case class BadRequest(msg: String) extends Fail

  case class InternalServerError(msg: String) extends Fail

  case object Forbidden extends Fail

  case class UnprocessableEntity(msg: String) extends Fail

  case object NotImplemented extends Fail
}
