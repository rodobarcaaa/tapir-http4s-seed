package com.example.books.infrastructure.slick

import com.example.books.domain.book.Book
import com.example.shared.infrastructure.slick.{DynamicSortPage, HasSlickPgProvider}

import java.util.UUID

trait BookMapping extends DynamicSortPage with AuthorMapping with PublisherMapping {
  self: HasSlickPgProvider =>

  import profile.api._

  final class BookTable(tag: Tag) extends Table[Book](tag, "books") with SortColumnSelector {

    def id          = column[UUID]("id", O.PrimaryKey)
    def isbn        = column[String]("isbn", O.Length(255), O.Unique)
    def title       = column[String]("title", O.Length(255), O.Unique)
    def description = column[String]("description")
    def year        = column[Int]("year")

    def publisherId = column[UUID]("publisher_id")

    def publisherFk = foreignKey("book_publisher_id_fk", publisherId, Publishers)(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Restrict
    )

    def authorId = column[UUID]("author_id")

    def authorFk = foreignKey("book_author_id_fk", authorId, Authors)(
      _.id,
      onUpdate = ForeignKeyAction.Restrict,
      onDelete = ForeignKeyAction.Restrict
    )

    override val select: Map[String, Rep[?]] = Map(
      "id"    -> id,
      "isbn"  -> isbn,
      "title" -> title,
      "year"  -> year
    )

    def * = (id, isbn, title, description, year, publisherId, authorId) <> (Book.tupled, Book.unapply)

  }

  val Books = TableQuery[BookTable]
}
