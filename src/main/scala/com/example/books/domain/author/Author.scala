package com.example.books.domain.author

import com.example.books.domain.common.{Id, Name}

import java.util.UUID

final case class Author(id: Id, firstName: Name, lastName: Name) {
  def completeName: String = s"$firstName $lastName"
}

object Author {

  // apply, unapply and tupled methods to use by slick table mapping

  def apply: (UUID, String, String) => Author = { case (id, firstName, lastName) =>
    Author(Id(id), Name(firstName), Name(lastName))
  }

  def unapply: Author => Option[(UUID, String, String)] = { author =>
    Some((author.id.value, author.firstName.value, author.lastName.value))
  }

  def tupled: ((UUID, String, String)) => Author = apply.tupled

}
