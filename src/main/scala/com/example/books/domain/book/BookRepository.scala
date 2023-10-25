package com.example.books.domain.book

import cats.effect.IO
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}

trait BookRepository {
  def upsert(book: Book): IO[Unit]

  def find(id: Id): IO[Option[Book]]

  def list(pr: PageRequest, filters: BookFilters): IO[PageResponse[Book]]

  def delete(id: Id): IO[Unit]

}
