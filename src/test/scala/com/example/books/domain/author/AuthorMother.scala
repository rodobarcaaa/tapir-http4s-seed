package com.example.books.domain.author

import com.example.shared.domain.common.{Id, Name}
import com.example.shared.domain.shared.{IdMother, TextMother}

object AuthorMother {
  def apply(
      id: Id = IdMother.random,
      firstName: Name = Name(TextMother.random(25)),
      lastName: Name = Name(TextMother.random(25))
  ): Author = Author(id, firstName, lastName)

  def random: Author = apply()
}
