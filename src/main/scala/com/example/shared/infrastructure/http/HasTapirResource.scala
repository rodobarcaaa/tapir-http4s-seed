package com.example.shared.infrastructure.http

import com.example.shared.domain.page.PageRequest
import io.circe.generic.AutoDerivation
import sttp.model.StatusCodes
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Tapir, TapirAliases}

import java.util.UUID

trait HasTapirResource
    extends Tapir
    with TapirAliases
    with TapirJsonCirce
    with AutoDerivation
    with StatusCodes
    with SchemaDerivation
    with HasTapirDocs
    with HasTapirRoutes {

  val pathId: EndpointInput.PathCapture[UUID] = path[UUID]("id")

  val pageDefault: EndpointInput[PageRequest] = sortPage()

  def page(defaultPage: Int = 1, defaultSize: Int = 15): EndpointInput[PageRequest] = {
    val page = query[Int]("page").default(defaultPage)
    val size = query[Int]("size").default(defaultSize)
    page.and(size).map(i => PageRequest(i._1, i._2))(p => (p.page, p.size))
  }

  val sortPageDefault: EndpointInput[PageRequest] = sortPage()

  def sortPage(
      sortingFields: Seq[String] = Seq("fields of entity"),
      defaultPage: Int = 1,
      defaultSize: Int = 15
  ): EndpointInput[PageRequest] = {
    val page = query[Int]("page").default(defaultPage)
    val size = query[Int]("size").default(defaultSize)
    val sort = query[Option[String]]("sort")
      .description(s"sorting values by ${sortingFields.mkString("(", ", ", ")")}, example: name (ASC) or -name (DESC)")

    page.and(size).and(sort).mapTo[PageRequest]
  }

  val baseEndpoint = endpoint.errorOut(
    oneOf[Fail](
      oneOfVariant(NotFound, jsonBody[Fail.NotFound]),
      oneOfVariant(Conflict, jsonBody[Fail.Conflict]),
      oneOfVariant(BadRequest, jsonBody[Fail.BadRequest.type]),
      oneOfVariant(BadRequest, jsonBody[Fail.IncorrectInput]),
      oneOfVariant(Unauthorized, jsonBody[Fail.Unauthorized]),
      oneOfVariant(Forbidden, jsonBody[Fail.Forbidden.type]),
      oneOfVariant(UnprocessableEntity, jsonBody[Fail.UnprocessableEntity.type]),
      oneOfVariant(InternalServerError, jsonBody[Fail.InternalServerError.type]),
      oneOfVariant(NotImplemented, jsonBody[Fail.NotImplemented.type])
    )
  )
}
