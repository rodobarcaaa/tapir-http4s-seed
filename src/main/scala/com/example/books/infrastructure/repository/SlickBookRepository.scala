package com.example.books.infrastructure.repository

import cats.effect.IO
import com.example.books.domain.book.{Book, BookFilters, BookRepository}
import com.example.books.domain.common.Id
import com.example.books.infrastructure.slick.BookMapping
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.slick.HasSlickPgProvider

class SlickBookRepository extends HasSlickPgProvider with BookRepository with BookMapping {

  import profile.api._

//  with MultiSortRepo
//
//  override type MultiTables = (BookTable, PublisherTable, AuthorTable)
//
//  override implicit def select(multiTables: MultiTables, field: String): Rep[_] = {
//    val (bookTable, publisherTable, authorTable) = multiTables
//
//    val columnFromBook = bookTable.select.get(field)
//    val columnFromPublisher = publisherTable.select.get(field)
//    val columnFromAuthor = authorTable.select.get(field)
//
//    columnFromBook.orElse(columnFromPublisher).orElse(columnFromAuthor).getOrElse {
//      throw new IllegalArgumentException(s"Sort field($field) not allowed")
//    }
//  }

  override def upsert(book: Book): IO[Unit] =
    db.runIOUnit(Books.insertOrUpdate(book))

  override def find(id: Id): IO[Option[Book]] =
    db.runIO(Books.filter(_.id === id.value).result.headOption)

  override def list(pr: PageRequest, filters: BookFilters): IO[PageResponse[Book]] = {
    val query = Books
      .filterOpt(filters.isbn)(_.isbn === _)
      .filterOpt(filters.year)(_.year === _)
      .filterOpt(filters.filter) { case (book, value) =>
        (book.title.toLowerCase like "%" + value.toLowerCase + "%") ||
          (book.description.toLowerCase like "%" + value.toLowerCase + "%")
      }
      .filterOpt(filters.publisherId)(_.publisherId === _)
      .filterOpt(filters.authorId)(_.authorId === _)

    db.runIO(query.withSortPage(pr))
  }

  override def delete(id: Id): IO[Unit] =
    db.runIOUnit(Books.filter(_.id === id.value).delete)

}
