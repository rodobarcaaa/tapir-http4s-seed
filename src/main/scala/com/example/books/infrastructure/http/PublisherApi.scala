package com.example.books.infrastructure.http

import com.example.auth.application.AuthService
import com.example.books.application.PublisherService
import com.example.books.domain.publisher.Publisher
import com.example.books.infrastructure.codecs.PublisherCodecs
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

import java.util.UUID

class PublisherApi(service: PublisherService, val authService: AuthService)
    extends HasTapirResource
    with PublisherCodecs
    with HasQueryFilter
    with HasJwtAuth {

  // Init
  private val base = baseEndpoint.tag("Publishers").in("publishers")

  //  Create a new publisher
  private val post = base.post
    .in(jwtAuth)
    .in(jsonBody[Publisher])
    .out(statusCode(Created))
    .serverLogic { case (token: String, publisher: Publisher) =>
      withAdminAuth(token) {
        service.create(publisher).orError
      }
    }

  //  Update a existing publisher
  private val put = base.put
    .in(pathId)
    .in(jwtAuth)
    .in(jsonBody[Publisher])
    .out(statusCode(NoContent))
    .serverLogic { case (id: UUID, token: String, publisher: Publisher) =>
      withAdminAuth(token) {
        service.update(Id(id), publisher).orError
      }
    }

  //  Get a publisher by id
  private val get = base.get
    .in(pathId)
    .in(jwtAuth)
    .out(jsonBody[Publisher])
    .serverLogic { case (id: UUID, token: String) =>
      withAuth(token) {
        service.find(Id(id)).orError(s"Publisher for id: $id Not Found")
      }
    }

  //  List publishers
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(Seq("name", "url"))

  private val list = base.get
    .in(sortPageFields / filter)
    .in(jwtAuth)
    .out(jsonBody[PageResponse[Publisher]])
    .serverLogic { case (pr: PageRequest, filter: Option[String], token: String) =>
      withAuth(token) {
        service.list(pr, filter).orError
      }
    }

  //  Delete a publisher by id
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
