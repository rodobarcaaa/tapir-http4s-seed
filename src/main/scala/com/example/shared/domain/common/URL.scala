package com.example.shared.domain.common

import cats.data.{Validated, ValidatedNel}

final case class URL(value: String) extends AnyVal {

  def validate(tag: String = "url"): ValidatedNel[String, Unit] =
    Validated.condNel(value.matches(URL.regexp), (), s"$tag must be in the correct format")

}

object URL {
  lazy val regexp =
    "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)"

}
