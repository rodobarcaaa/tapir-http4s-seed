package com.example.books.domain.author

import cats.effect.IO
import com.example.books.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}

trait AuthorRepository {
  def upsert(author: Author): IO[Unit]

  def find(id: Id): IO[Option[Author]]

  def list(pr: PageRequest, filter: Option[String]): IO[PageResponse[Author]]

  def delete(id: Id): IO[Unit]

}
