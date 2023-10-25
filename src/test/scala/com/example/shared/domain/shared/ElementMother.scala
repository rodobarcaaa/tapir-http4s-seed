package com.example.shared.domain.shared

import scala.util.Random

object ElementMother {
  def random[A](seq: Seq[A]): A = seq(Random.nextInt(seq.length))
}
