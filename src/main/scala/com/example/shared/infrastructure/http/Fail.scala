package com.example.shared.infrastructure.http

abstract class Fail extends Exception

object Fail {
  case class NotFound(msg: String)       extends Fail
  case class Conflict(msg: String)       extends Fail
  case class IncorrectInput(msg: String) extends Fail
  case class Unauthorized(msg: String)   extends Fail
  case object BadRequest                 extends Fail
  case object Forbidden                  extends Fail
  case object UnprocessableEntity        extends Fail
  case object InternalServerError        extends Fail
  case object NotImplemented             extends Fail
}


//import com.alejandrohdezma.tapir._
//import sttp.tapir.generic.auto._
//import sttp.tapir.Schema
//import io.circe.generic.extras.Configuration
//import io.circe.generic.extras.ConfiguredJsonCodec
//import sttp.model.StatusCode._
//
//@ConfiguredJsonCodec sealed trait MyError
//@code(NotFound) final case class UserNotFound(name: String) extends MyError
//@code(Forbidden) final case class WrongUser(id: String) extends MyError
//@code(Forbidden) final case class WrongPassword(id: String) extends MyError
//
//object MyError {
//
//  implicit val config: Configuration =
//    Configuration.default.withDiscriminator("error")
//
//  implicit lazy val MyErrorSchema: Schema[MyError] = Schema.derived[MyError].addDiscriminator("error")
//
//}
