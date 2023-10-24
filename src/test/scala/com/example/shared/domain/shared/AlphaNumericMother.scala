package com.example.shared.domain.shared

import scala.util.Random

object AlphaNumericMother {
  val defaultChars = 25

  def random(numChars: Int = defaultChars): String = Random.alphanumeric take numChars mkString ""
}
