package com.example.books.infrastructure.http

import com.example.books.application.BookService
import com.example.books.domain.book.{Book, BookFilters}
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

import java.util.UUID

class BookApi(service: BookService) extends HasTapirResource with BookCodecs with HasQueryFilter {

  // Init
  private val base = baseEndpoint.tag("Books").in("books")

  //  Create a new book
  private val post = base.post
    .in(jsonBody[Book])
    .out(statusCode(Created))
    .serverLogic { book => service.create(book).orError }

  //  Update a existing book
  private val put = base.put
    .in(pathId)
    .in(jsonBody[Book])
    .out(statusCode(NoContent))
    .serverLogic { case (id, book) => service.update(Id(id), book).orError }

  //  Get a book by id
  private val get = base.get
    .in(pathId)
    .out(jsonBody[Book])
    .serverLogic { id => service.find(Id(id)).orError(s"Book for id: $id Not Found") }

  //  List books
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(
    Seq("isbn", "title", "year", "publisherName", "authorName")
  )

  private val filterFields = {
    val isbn        = query[Option[String]]("isbn")
    val year        = query[Option[Int]]("year")
    val publisherId = query[Option[UUID]]("publisherId")
    val authorId    = query[Option[UUID]]("authorId")

    filter.and(isbn).and(year).and(publisherId).and(authorId).mapTo[BookFilters]
  }

  private val list = base.get
    .in(sortPageFields / filterFields)
    .out(jsonBody[PageResponse[Book]])
    .serverLogic { case (pr, filters) => service.list(pr, filters).orError }

  //  Delete a book by id
  private val delete = base.delete
    .in(pathId)
    .out(statusCode(NoContent))
    .serverLogic { id => service.delete(Id(id)).orError }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
