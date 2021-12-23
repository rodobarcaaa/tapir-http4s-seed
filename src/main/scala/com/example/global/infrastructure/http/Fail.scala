package com.example.global.infrastructure.http

//abstract class Fail extends Exception

object Fail {
  case class NotFound(msg: String) /*extends Fail*/
//  case class Conflict(msg: String) extends Fail
//  case class IncorrectInput(msg: String) extends Fail
//  case class Unauthorized(msg: String) extends Fail
//  case object Forbidden extends Fail
}
