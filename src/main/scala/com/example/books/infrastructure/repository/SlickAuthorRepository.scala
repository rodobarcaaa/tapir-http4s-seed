package com.example.books.infrastructure.repository

import cats.effect.IO
import com.example.books.domain.author.{Author, AuthorRepository}
import com.example.books.infrastructure.slick.AuthorMapping
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.slick.HasSlickPgProvider

class SlickAuthorRepository extends HasSlickPgProvider with AuthorRepository with AuthorMapping {

  import profile.api._

  override def upsert(author: Author): IO[Unit] =
    db.runIOUnit(Authors.insertOrUpdate(author))

  override def find(id: Id): IO[Option[Author]] =
    db.runIO(Authors.filter(_.id === id.value).result.headOption)

  override def list(pr: PageRequest, filter: Option[String]): IO[PageResponse[Author]] = {
    val query = Authors.filterOpt(filter) { case (author, value) =>
      (author.firstName.toLowerCase like "%" + value.toLowerCase + "%") ||
        (author.lastName.toLowerCase like "%" + value.toLowerCase + "%")
    }

    db.runIO(query.withSortPage(pr))
  }

  override def delete(id: Id): IO[Unit] =
    db.runIOUnit(Authors.filter(_.id === id.value).delete)

}
