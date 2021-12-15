package com.example.books.application

import cats.effect.IO
import com.example.books.domain._

final class BookService(bookRepository: BookRepository) {

  def create(book: Book): IO[BookId]           = bookRepository.create(book)
  def find(id: BookId): IO[Option[Book]]       = bookRepository.find(id)
  def list: IO[List[Book]]                     = bookRepository.list
  def update(id: BookId, book: Book): IO[Unit] = bookRepository.update(id, book)
  def delete(id: BookId): IO[Unit]             = bookRepository.delete(id)

}
