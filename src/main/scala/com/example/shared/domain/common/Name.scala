package com.example.shared.domain.common

import cats.data.{Validated, ValidatedNel}
import cats.implicits.catsSyntaxTuple2Semigroupal

final case class Name(value: String) extends AnyVal {

  def validate(tag: String = "name", maxLength: Int = Name.maxLength): ValidatedNel[String, Unit] = (
    HasValidations.validateEmpty(value, tag),
    Validated.condNel(value.length <= maxLength, (), s"$tag max length should be $maxLength characters")
  ).mapN((_, _) => ())

}

object Name {

  lazy val maxLength: Int = 255

}
