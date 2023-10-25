package com.example.shared.domain.shared

object SeqMother {
  val maximumElements = 10

  def randomOfNum[T](apply: => T, elements: Int = maximumElements): Seq[T] = (1 to elements).map(_ => apply)
}
