package com.example.books.application

import cats.effect.IO
import com.example.books.domain.book.{Book, BookFilters, BookRepository}
import com.example.shared.application.CommonService
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}

final class BookService(repo: BookRepository) extends CommonService {

  private def upsert(book: Book) = validateRequest(book) *> repo.upsert(book)

  def create(book: Book): IO[Unit] = upsert(book)

  def update(id: Id, book: Book): IO[Unit] = upsert(book.copy(id = id))

  def find(id: Id): IO[Option[Book]] = repo.find(id)

  def list(pr: PageRequest, filters: BookFilters): IO[PageResponse[Book]] = repo.list(pr, filters)

  def delete(id: Id): IO[Unit] = repo.delete(id)

}
