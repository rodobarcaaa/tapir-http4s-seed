package com.example.shared.domain.shared

import scala.util.Random

object TextMother {
  val defaultChars = 25

  def random(numChars: Int = defaultChars): String = alpha take numChars mkString ""

  def alpha: LazyList[Char] = {
    def nextAlpha: Char = {
      val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
      chars charAt (Random nextInt chars.length)
    }

    LazyList continually nextAlpha
  }
}
