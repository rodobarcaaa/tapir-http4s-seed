package com.example.shared.domain.common

import cats.data.{Validated, ValidatedNel}

trait HasValidated {
  def validated: ValidatedNel[String, Unit]
}

object HasValidations {

  def validateEmpty(value: String, tag: String) = {
    Validated.condNel(value.nonEmpty, (), s"$tag should not be empty")
  }

}
