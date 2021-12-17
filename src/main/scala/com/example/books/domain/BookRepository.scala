package com.example.books.domain

import cats.effect.IO

import java.util.concurrent.atomic.AtomicReference

trait BookRepository {
  def books: AtomicReference[List[Book]]

  def create(book: Book): IO[BookId]

  def find(id: BookId): IO[Option[Book]]

  def list: IO[List[Book]]

  def update(id: BookId, book: Book): IO[Unit]

  def delete(id: BookId): IO[Unit]

}
