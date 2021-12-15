package com.example.books.domain

import cats.effect.IO

trait BookRepository {

  def create(book: Book): IO[BookId]

  def find(id: BookId): IO[Option[Book]]

  def list: IO[List[Book]]

  def update(id: BookId, book: Book): IO[Unit]

  def delete(id: BookId): IO[Unit]

}
