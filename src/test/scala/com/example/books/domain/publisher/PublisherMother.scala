package com.example.books.domain.publisher

import com.example.shared.domain.common.{Id, Name, URL}
import com.example.shared.domain.shared.{IdMother, TextMother}

object PublisherMother {
  def apply(
      id: Id = IdMother.random,
      name: Name = Name(TextMother.random(25)),
      url: URL = URL(s"www.${TextMother.random(10)}.com")
  ): Publisher = Publisher(id, name, url)

  def random: Publisher = apply()
}
