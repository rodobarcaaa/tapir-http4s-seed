package com.example.shared.infrastucture.http

import sttp.model.StatusCodes
import sttp.tapir.generic.SchemaDerivation
import sttp.tapir.json.circe.TapirJsonCirce
import sttp.tapir.{Tapir, TapirAliases}

trait HasTapirResource
    extends Tapir
    with TapirAliases
    with TapirJsonCirce
    with StatusCodes
    with SchemaDerivation
    with HasTapirDocs
    with HasTapirRoutes
