package com.example.books.application

import cats.effect.IO
import com.example.books.domain.publisher.{Publisher, PublisherRepository}
import com.example.books.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}

final class PublisherService(repo: PublisherRepository) {

  def create(publisher: Publisher): IO[Unit] = repo.upsert(publisher)

  def update(id: Id, publisher: Publisher): IO[Unit] = repo.upsert(publisher.copy(id = id))

  def find(id: Id): IO[Option[Publisher]] = repo.find(id)

  def list(pr: PageRequest, filter: Option[String]): IO[PageResponse[Publisher]] = repo.list(pr, filter)

  def delete(id: Id): IO[Unit] = repo.delete(id)

}
