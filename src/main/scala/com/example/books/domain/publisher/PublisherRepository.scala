package com.example.books.domain.publisher

import cats.effect.IO
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}

trait PublisherRepository {
  def upsert(publisher: Publisher): IO[Unit]

  def find(id: Id): IO[Option[Publisher]]

  def list(pr: PageRequest, filter: Option[String]): IO[PageResponse[Publisher]]

  def delete(id: Id): IO[Unit]

}
