package com.example.shared.infrastucture.http

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
    with HasTapirRoutes
