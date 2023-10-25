package com.example.books.domain.book

import com.example.shared.domain.common.Id
import com.example.shared.domain.shared.{AlphaNumericMother, IdMother, TextMother}

import java.time.LocalDate

object BookMother {
  def apply(
      authorId: Id,
      publisherId: Id,
      id: Id = IdMother.random,
      title: BookTitle = BookTitle(TextMother.random(100)),
      isbn: BookIsbn = BookIsbn(AlphaNumericMother.random(30)),
      description: BookDescription = BookDescription(TextMother.random(500)),
      yearMother: BookYear = BookYear(LocalDate.now().getYear)
  ): Book = Book(id, isbn, title, description, yearMother, publisherId, authorId)

  def random(authorId: Id, publisherId: Id): Book = apply(authorId, publisherId)
}
