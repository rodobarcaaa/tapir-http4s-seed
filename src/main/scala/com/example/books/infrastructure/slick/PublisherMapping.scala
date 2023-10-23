package com.example.books.infrastructure.slick

import com.example.books.domain.publisher.Publisher
import com.example.shared.infrastructure.slick.{DynamicSortPage, HasSlickPgProvider}

import java.util.UUID

trait PublisherMapping extends DynamicSortPage {
  self: HasSlickPgProvider =>

  import profile.api._

  final class PublisherTable(tag: Tag) extends Table[Publisher](tag, "publishers") with SortColumnSelector {

    def id = column[UUID]("id", O.PrimaryKey)

    def name = column[String]("name", O.Length(255), O.Unique)

    def url = column[String]("url", O.Length(255), O.Unique)

    override val select: Map[String, Rep[_]] = Map(
      "id"            -> id,
      "name"          -> name,
      "publisherName" -> name,
      "url"           -> url
    )

    def * = (id, name, url) <> (Publisher.tupled, Publisher.unapply)

  }

  val Publishers = TableQuery[PublisherTable]
}
