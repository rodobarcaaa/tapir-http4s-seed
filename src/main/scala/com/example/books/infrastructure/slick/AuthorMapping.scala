package com.example.books.infrastructure.slick

import com.example.books.domain.author.Author
import com.example.shared.infrastructure.slick.{DynamicSortPage, HasSlickPgProvider}

import java.util.UUID

trait AuthorMapping extends DynamicSortPage {
  self: HasSlickPgProvider =>

  import profile.api._

  final class AuthorTable(tag: Tag) extends Table[Author](tag, "authors") with SortColumnSelector {

    def id        = column[UUID]("id", O.PrimaryKey)
    def firstName = column[String]("first_name", O.Length(255))
    def lastName  = column[String]("last_name", O.Length(255))

    def completeName = firstName ++ " " ++ lastName

    def idx = index("idx_authors_unique_names", (firstName, lastName), unique = true)

    override val select: Map[String, Rep[_]] = Map(
      "id"         -> id,
      "firstName"  -> firstName,
      "lastName"   -> lastName,
      "authorName" -> completeName
    )

    def * = (id, firstName, lastName) <> (Author.tupled, Author.unapply)

  }

  val Authors = TableQuery[AuthorTable]
}
