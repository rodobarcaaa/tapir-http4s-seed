package com.example.books.infrastructure.http

import com.example.auth.application.AuthService
import com.example.books.application.AuthorService
import com.example.books.domain.author.Author
import com.example.books.infrastructure.codecs.AuthorCodecs
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

import java.util.UUID

class AuthorApi(service: AuthorService, val authService: AuthService)
    extends HasTapirResource
    with AuthorCodecs
    with HasQueryFilter
    with HasJwtAuth {

  // Init
  private val base = baseEndpoint.tag("Authors").in("authors")

  //  Create a new author
  private val post = base.post
    .in(jwtAuth)
    .in(jsonBody[Author])
    .out(statusCode(Created))
    .serverLogic { case (token: String, author: Author) =>
      withAdminAuth(token) {
        service.create(author).orError
      }
    }

  //  Update a existing author
  private val put = base.put
    .in(pathId)
    .in(jwtAuth)
    .in(jsonBody[Author])
    .out(statusCode(NoContent))
    .serverLogic { case (id: UUID, token: String, author: Author) =>
      withAdminAuth(token) {
        service.update(Id(id), author).orError
      }
    }

  //  Get a author by id
  private val get = base.get
    .in(pathId)
    .in(jwtAuth)
    .out(jsonBody[Author])
    .serverLogic { case (id: UUID, token: String) =>
      withAuth(token) {
        service.find(Id(id)).orError(s"Author for id: $id Not Found")
      }
    }

  //  List authors
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(Seq("firstName", "lastName"))

  private val list = base.get
    .in(sortPageFields / filter)
    .in(jwtAuth)
    .out(jsonBody[PageResponse[Author]])
    .serverLogic { case (pr: PageRequest, filter: Option[String], token: String) =>
      withAuth(token) {
        service.list(pr, filter).orError
      }
    }

  //  Delete a author by id
  private val delete = base.delete
    .in(pathId)
    .in(jwtAuth)
    .out(statusCode(NoContent))
    .serverLogic { case (id: UUID, token: String) =>
      withAdminAuth(token) {
        service.delete(Id(id)).orError
      }
    }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
