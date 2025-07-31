package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.auth.application.AuthService
import com.example.auth.domain.RoleAuthorization
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
      validateJwtToken(token).flatMap {
        case Right(authUser) => 
          RoleAuthorization.requireAdmin(authUser.user) match {
            case Right(_) => service.create(author).orError
            case Left(error) => IO.pure(Left(error))
          }
        case Left(error) => IO.pure(Left(error))
      }
    }

  //  Update a existing author
  private val put = base.put
    .in(pathId)
    .in(jwtAuth)
    .in(jsonBody[Author])
    .out(statusCode(NoContent))
    .serverLogic { case (id: UUID, token: String, author: Author) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => 
          RoleAuthorization.requireAdmin(authUser.user) match {
            case Right(_) => service.update(Id(id), author).orError
            case Left(error) => IO.pure(Left(error))
          }
        case Left(error) => IO.pure(Left(error))
      }
    }

  //  Get a author by id
  private val get = base.get
    .in(pathId)
    .in(jwtAuth)
    .out(jsonBody[Author])
    .serverLogic { case (id: UUID, token: String) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => service.find(Id(id)).orError(s"Author for id: $id Not Found")
        case Left(error) => IO.pure(Left(error))
      }
    }

  //  List authors
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(Seq("firstName", "lastName"))

  private val list = base.get
    .in(sortPageFields / filter)
    .in(jwtAuth)
    .out(jsonBody[PageResponse[Author]])
    .serverLogic { case (pr: PageRequest, filter: Option[String], token: String) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => service.list(pr, filter).orError
        case Left(error) => IO.pure(Left(error))
      }
    }

  //  Delete a author by id
  private val delete = base.delete
    .in(pathId)
    .in(jwtAuth)
    .out(statusCode(NoContent))
    .serverLogic { case (id: UUID, token: String) =>
      validateJwtToken(token).flatMap {
        case Right(authUser) => 
          RoleAuthorization.requireAdmin(authUser.user) match {
            case Right(_) => service.delete(Id(id)).orError
            case Left(error) => IO.pure(Left(error))
          }
        case Left(error) => IO.pure(Left(error))
      }
    }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
