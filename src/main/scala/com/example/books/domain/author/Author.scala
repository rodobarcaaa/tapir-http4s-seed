package com.example.books.domain.author

import cats.data.ValidatedNel
import cats.implicits.catsSyntaxTuple2Semigroupal
import com.example.books.domain.common.{HasValidated, Id, Name}

import java.util.UUID

final case class Author(id: Id, firstName: Name, lastName: Name) extends HasValidated {
  def completeName: String = s"$firstName $lastName"


  override def validated: ValidatedNel[String, Unit] = (
    firstName.validate("firstName", Author.maxLengthFirstName),
    lastName.validate("lastName", Author.maxLengthLastName)
  ).mapN((_, _) => ())
}

object Author {

  private lazy val maxLengthFirstName = 255
  private lazy val maxLengthLastName = 255

  // apply, unapply and tupled methods to use by slick table mapping

  def apply: (UUID, String, String) => Author = {
    case (id, firstName, lastName) =>
      Author(Id(id), Name(firstName), Name(lastName))
  }

  def unapply: Author => Option[(UUID, String, String)] = { author =>
    Some((author.id.value, author.firstName.value, author.lastName.value))
  }

  def tupled: ((UUID, String, String)) => Author = apply.tupled

}
