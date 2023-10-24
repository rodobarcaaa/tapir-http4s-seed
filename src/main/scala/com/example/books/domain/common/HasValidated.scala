package com.example.books.domain.common

import cats.data.ValidatedNel

trait HasValidated {
  def validated: ValidatedNel[String, Unit]
}
