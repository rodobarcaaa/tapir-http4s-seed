package com.example.books.domain.common

import cats.data.{Validated, ValidatedNel}
import cats.implicits.catsSyntaxTuple2Semigroupal

final case class Name(value: String) extends AnyVal {

  def validate(tag: String, maxLength: Int): ValidatedNel[String, Unit] = {
    (
      Validated.condNel(value.nonEmpty, (), s"$tag should be not empty"),
      Validated.condNel(value.length <= maxLength, (), s"$tag max length should be $maxLength characters")
    ).mapN((_, _) => ())
  }
}
