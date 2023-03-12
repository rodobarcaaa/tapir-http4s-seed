package com.example.books.domain

import java.util.UUID

final case class BookId(value: UUID)      extends AnyVal
final case class BookTitle(value: String) extends AnyVal
final case class Year(value: Int)         extends AnyVal
final case class Author(value: String)    extends AnyVal
final case class Book(id: BookId, title: BookTitle, year: Year, author: Author)
