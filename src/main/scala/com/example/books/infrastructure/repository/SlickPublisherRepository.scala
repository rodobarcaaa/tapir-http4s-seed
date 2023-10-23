package com.example.books.infrastructure.repository

import cats.effect.IO
import com.example.books.domain.common.Id
import com.example.books.domain.publisher.{Publisher, PublisherRepository}
import com.example.books.infrastructure.slick.PublisherMapping
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.slick.HasSlickPgProvider

class SlickPublisherRepository extends HasSlickPgProvider with PublisherRepository with PublisherMapping {

  import profile.api._

  override def upsert(publisher: Publisher): IO[Unit] =
    db.runIOUnit(Publishers.insertOrUpdate(publisher))

  override def find(id: Id): IO[Option[Publisher]] =
    db.runIO(Publishers.filter(_.id === id.value).result.headOption)

  override def list(pr: PageRequest, filter: Option[String]): IO[PageResponse[Publisher]] = {
    val query = Publishers.filterOpt(filter) { case (publisher, value) =>
      (publisher.name.toLowerCase like "%" + value.toLowerCase + "%") ||
        (publisher.url.toLowerCase like "%" + value.toLowerCase + "%")
    }

    db.runIO(query.withSortPage(pr))
  }

  override def delete(id: Id): IO[Unit] =
    db.runIOUnit(Publishers.filter(_.id === id.value).delete)

}
