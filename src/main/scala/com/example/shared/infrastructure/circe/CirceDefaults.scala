package com.example.shared.infrastructure.circe

trait CirceDefaults {

  import io.circe.generic.extras.Configuration
  implicit val config: Configuration = Configuration.default

}
