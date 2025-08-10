package com.example.books.domain.book

import java.util.UUID

final case class BookFilters(
    filter: Option[String] = None,
    isbn: Option[String] = None,
    year: Option[Int] = None,
    publisherId: Option[UUID] = None,
    authorId: Option[UUID] = None
)

object BookFilters {
  val empty: BookFilters = BookFilters()
}
