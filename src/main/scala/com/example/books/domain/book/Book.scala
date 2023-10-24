package com.example.books.domain.book

import cats.data._
import cats.implicits._
import com.example.books.domain.book.Book._
import com.example.shared.domain.common.{HasValidated, HasValidations, Id}

import java.time.LocalDate
import java.util.UUID

final case class BookTitle(value: String) extends AnyVal {

  def validate(tag: String = "title", maxLength: Int = maxLengthMediumText): ValidatedNel[String, Unit] = (
    HasValidations.validateEmpty(value, tag),
    Validated.condNel(value.length <= maxLength, (), s"$tag max length should be $maxLength characters")
  ).mapN((_, _) => ())

}

final case class BookIsbn(value: String) extends AnyVal {

  def validate(tag: String = "isbn", maxLength: Int = maxLengthSmallText): ValidatedNel[String, Unit] = (
    HasValidations.validateEmpty(value, tag),
    Validated.condNel(value.length <= maxLength, (), s"$tag max length should be $maxLength characters")
  ).mapN((_, _) => ())

}

final case class BookDescription(value: String) extends AnyVal {

  def validate(tag: String = "description", maxLength: Int = maxLengthLongText): ValidatedNel[String, Unit] = (
    HasValidations.validateEmpty(value, tag),
    Validated.condNel(value.length <= maxLength, (), s"$tag max length should be $maxLength characters")
  ).mapN((_, _) => ())

}

final case class BookYear(value: Int) extends AnyVal {

  def validate(tag: String = "year"): ValidatedNel[String, Unit] = {
    val currentYear = LocalDate.now.getYear
    Validated.condNel(
      value.toString.length == 4 && value >= 1900 && value <= currentYear,
      (),
      s"$tag should be a valid year"
    )
  }

}

final case class Book(
    id: Id,
    isbn: BookIsbn,
    title: BookTitle,
    description: BookDescription,
    year: BookYear,
    publisherId: Id,
    authorId: Id
) extends HasValidated {

  override def validated: ValidatedNel[String, Unit] = (
    isbn.validate(),
    title.validate(),
    description.validate(),
    year.validate()
  ).mapN((_, _, _, _) => ())

}

object Book {

  lazy val maxLengthSmallText: Int  = 35
  lazy val maxLengthMediumText: Int = 255
  lazy val maxLengthLongText: Int   = 1255

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
