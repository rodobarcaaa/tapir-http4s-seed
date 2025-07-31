package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.books.application.AuthorService
import com.example.books.domain.author.Author
import com.example.books.infrastructure.codecs.AuthorCodecs
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

class AuthorApi(service: AuthorService, val authService: AuthService) extends HasTapirResource with AuthorCodecs with HasQueryFilter with HasJwtAuth {

  // Init
  private val base = baseEndpoint.tag("Authors").in("authors")

  //  Create a new author
  private val post = base.post
    .in(jwtAuth)
    .in(jsonBody[Author])
    .out(statusCode(Created))
    .serverLogic { case (token, author) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => service.create(author).orError
        case Left(error)     => IO.pure(Left(error))
      }
    }

  //  Update a existing author
  private val put = base.put
    .in(pathId)
    .in(jwtAuth)
    .in(jsonBody[Author])
    .out(statusCode(NoContent))
    .serverLogic { case ((id, token), author) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => service.update(Id(id), author).orError
        case Left(error)     => IO.pure(Left(error))
      }
    }

  //  Get a author by id
  private val get = base.get
    .in(pathId)
    .out(jsonBody[Author])
    .serverLogic { id => service.find(Id(id)).orError(s"Author for id: $id Not Found") }

  //  List authors
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(Seq("firstName", "lastName"))

  private val list = base.get
    .in(sortPageFields / filter)
    .out(jsonBody[PageResponse[Author]])
    .serverLogic { case (pr, filter) => service.list(pr, filter).orError }

  //  Delete a author by id
  private val delete = base.delete
    .in(pathId)
    .in(jwtAuth)
    .out(statusCode(NoContent))
    .serverLogic { case (id, token) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => service.delete(Id(id)).orError
        case Left(error)     => IO.pure(Left(error))
      }
    }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
