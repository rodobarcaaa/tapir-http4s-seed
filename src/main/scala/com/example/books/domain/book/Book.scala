package com.example.books.domain.book

import com.example.books.domain.common.Id

import java.util.UUID

final case class BookTitle(value: String)       extends AnyVal
final case class BookIsbn(value: String)        extends AnyVal
final case class BookYear(value: Int)           extends AnyVal
final case class BookDescription(value: String) extends AnyVal

final case class Book(
    id: Id,
    isbn: BookIsbn,
    title: BookTitle,
    description: BookDescription,
    year: BookYear,
    publisherId: Id,
    authorId: Id
)

object Book {

  // apply, unapply and tupled methods to use by slick table mapping

  def apply: (UUID, String, String, String, Int, UUID, UUID) => Book = {
    case (id, isbn, title, description, year, publisherId, authorId) =>
      Book(
        Id(id),
        BookIsbn(isbn),
        BookTitle(title),
        BookDescription(description),
        BookYear(year),
        Id(publisherId),
        Id(authorId)
      )
  }

  def unapply: Book => Option[(UUID, String, String, String, Int, UUID, UUID)] = { book =>
    Some(
      (
        book.id.value,
        book.isbn.value,
        book.title.value,
        book.description.value,
        book.year.value,
        book.publisherId.value,
        book.authorId.value
      )
    )
  }

  def tupled: ((UUID, String, String, String, Int, UUID, UUID)) => Book = apply.tupled

}
