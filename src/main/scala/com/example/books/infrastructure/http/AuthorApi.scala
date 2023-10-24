package com.example.books.infrastructure.http

import com.example.books.application.AuthorService
import com.example.books.domain.author.Author
import com.example.books.domain.common.Id
import com.example.books.infrastructure.codecs.AuthorCodecs
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

class AuthorApi(service: AuthorService) extends HasTapirResource with AuthorCodecs with HasQueryFilter {

  // Init
  private val base = baseEndpoint.tag("Authors").in("authors")

  //  Create a new author
  private val post = base.post
    .in(jsonBody[Author])
    .out(statusCode(Created))
    .serverLogic { author => service.create(author).orError }

  //  Update a existing author
  private val put = base.put
    .in(pathId)
    .in(jsonBody[Author])
    .out(statusCode(NoContent))
    .serverLogic { case (id, author) => service.update(Id(id), author).orError }

  //  Get a author by id
  private val get = base.get
    .in(pathId)
    .out(jsonBody[Author])
    .serverLogic { id => service.find(Id(id)).orError(s"Author for id: $id Not Found") }

  //  List authors
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(
    Seq("isbn", "title", "year", "publisherName", "authorName")
  )

  private val list = base.get
    .in(sortPageFields / filter)
    .out(jsonBody[PageResponse[Author]])
    .serverLogic { case (pr, filter) => service.list(pr, filter).orError }

  //  Delete a author by id
  private val delete = base.delete
    .in(pathId)
    .out(statusCode(NoContent))
    .serverLogic { id => service.delete(Id(id)).orError }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
