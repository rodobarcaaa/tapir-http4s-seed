package com.example.shared.infrastructure.http

import io.circe.generic.AutoDerivation
import sttp.model.StatusCodes
import sttp.tapir.generic.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Tapir, TapirAliases}

trait HasTapirResource
    extends Tapir
    with TapirAliases
    with TapirJsonCirce
    with AutoDerivation
    with StatusCodes
    with SchemaDerivation
    with HasTapirDocs
    with HasTapirRoutes {

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
