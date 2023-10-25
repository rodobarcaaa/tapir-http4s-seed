package com.example.books.domain.publisher

import cats.data.ValidatedNel
import cats.implicits._
import com.example.shared.domain.common.{HasValidated, Id, Name, URL}

import java.util.UUID

final case class Publisher(id: Id, name: Name, url: URL) extends HasValidated {

  override def validated: ValidatedNel[String, Unit] = (name.validate(), url.validate()).mapN((_, _) => ())
}

object Publisher {

  // apply, unapply and tupled methods to use by slick table mapping

  def apply: (UUID, String, String) => Publisher = { case (id, name, url) =>
    Publisher(Id(id), Name(name), URL(url))
  }

  def unapply: Publisher => Option[(UUID, String, String)] = { publisher =>
    Some((publisher.id.value, publisher.name.value, publisher.url.value))
  }

  def tupled: ((UUID, String, String)) => Publisher = apply.tupled
}
