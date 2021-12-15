package com.example.books.infrastructure.repository

import cats.effect.IO
import com.example.books.domain._

import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

final class MemoryBookRepository extends BookRepository {

  val books = new AtomicReference(
    List(
      Book(BookId(UUID.randomUUID), "The Sorrows of Young Werther", 1774, Author("Johann Wolfgang von Goethe")),
      Book(BookId(UUID.randomUUID), "Iliad", -8000, Author("Homer")),
      Book(BookId(UUID.randomUUID), "Nad Niemnem", 1888, Author("Eliza Orzeszkowa")),
      Book(BookId(UUID.randomUUID), "The Colour of Magic", 1983, Author("Terry Pratchett")),
      Book(BookId(UUID.randomUUID), "The Art of Computer Programming", 1968, Author("Donald Knuth")),
      Book(BookId(UUID.randomUUID), "Pharaoh", 1897, Author("Boleslaw Prus"))
    )
  )

  def create(book: Book): IO[BookId] = IO {
    books.getAndUpdate(_ :+ book)
    book.id
  }

  def find(id: BookId): IO[Option[Book]] = IO {
    books.get().find(_.id == id)
  }

  def list: IO[List[Book]] = IO(books.get())

  def update(id: BookId, book: Book): IO[Unit] = IO {
    books.getAndUpdate { books =>
      val exists = books.exists(_.id == id)
      if (exists) books.filterNot(_.id == id) :+ book.copy(id = id) else books
    }: Unit
  }

  def delete(id: BookId): IO[Unit] = IO {
    books.getAndUpdate(_.filterNot(_.id == id))
  }
}
