package com.example.shared.infrastructure.http

import cats.effect.IO
import com.example.shared.domain.page.PageRequest
import io.circe.generic.AutoDerivation
import sttp.model.StatusCodes
import sttp.tapir.generic.auto.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Tapir, TapirAliases}

import java.sql.SQLException
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
      oneOfVariant(BadRequest, jsonBody[Fail.BadRequest]),
      oneOfVariant(BadRequest, jsonBody[Fail.IncorrectInput]),
      oneOfVariant(Unauthorized, jsonBody[Fail.Unauthorized]),
      oneOfVariant(InternalServerError, jsonBody[Fail.InternalServerError]),
      oneOfVariant(Forbidden, jsonBody[Fail.Forbidden.type]),
      oneOfVariant(UnprocessableEntity, jsonBody[Fail.UnprocessableEntity]),
      oneOfVariant(NotImplemented, jsonBody[Fail.NotImplemented.type])
    )
  )

  private def ioOrError[T](io: IO[T]): IO[Either[Fail, T]] = io.map(Right(_)).handleError {
    case th: Fail                      => Left(th)
    case _: NotImplementedError        => Left(Fail.NotImplemented)
    case iae: IllegalArgumentException => Left(Fail.BadRequest(iae.getMessage))
    case sqlEx: SQLException           =>
      val message = sqlEx.getMessage.split("Detail:").lastOption.getOrElse("SQL Conflict")
      Left(Fail.Conflict(message))
    case th: Throwable                 =>
      if (th.getMessage != null && th.getMessage.contains("requirement failed")) {
        Left(Fail.BadRequest(th.getMessage))
      } else {
        val msg = s"Error[${UUID.randomUUID().toString.take(10)}]: contact support"
        Left(Fail.InternalServerError(msg))
      }

  }

  implicit class ServerEndpointsLogicOps[T](io: IO[T]) {
    def orError: IO[Either[Fail, T]] = ioOrError(io)
  }

  implicit class OptionEndpointsLogicOps[T](io: IO[Option[T]]) {
    def orError(msg: String): IO[Either[Fail, T]] = io.map(_.toRight(Fail.NotFound(msg): Fail))
  }

}
