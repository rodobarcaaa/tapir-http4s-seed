package com.example.books.infrastructure.http

import com.example.books.application.PublisherService
import com.example.books.domain.common.Id
import com.example.books.domain.publisher.Publisher
import com.example.books.infrastructure.codecs.PublisherCodecs
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.http._

class PublisherApi(service: PublisherService) extends HasTapirResource with PublisherCodecs with HasQueryFilter {

  // Init
  private val base = baseEndpoint.tag("Publishers").in("publishers")

  //  Create a new publisher
  private val post = base.post
    .in(jsonBody[Publisher])
    .out(statusCode(Created))
    .serverLogicSuccess { publisher =>
      service.create(publisher)
    }

  //  Update a existing publisher
  private val put = base.put
    .in(pathId)
    .in(jsonBody[Publisher])
    .out(statusCode(NoContent))
    .serverLogicSuccess { case (id, publisher) =>
      service.update(Id(id), publisher)
    }

  //  Get a publisher by id
  private val get = base.get
    .in(pathId)
    .out(jsonBody[Publisher])
    .serverLogic { id =>
      service.find(Id(id)).map(_.toRight(Fail.NotFound(s"Publisher for id: $id Not Found"): Fail))
    }

  //  List publishers
  private val sortPageFields: EndpointInput[PageRequest] = sortPage(Seq("name", "url"))

  private val list = base.get
    .in(sortPageFields / filter)
    .out(jsonBody[PageResponse[Publisher]])
    .serverLogicSuccess { case (pr, filter) => service.list(pr, filter) }

  //  Delete a publisher by id
  private val delete = base.delete
    .in(pathId)
    .out(statusCode(NoContent))
    .serverLogicSuccess { id => service.delete(Id(id)) }

  // Endpoints to Expose
  override val endpoints: ServerEndpoints = List(post, put, get, list, delete)
}
