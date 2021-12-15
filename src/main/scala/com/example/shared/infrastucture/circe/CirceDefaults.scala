package com.example.shared.infrastucture.circe

trait CirceDefaults {

  import io.circe.generic.extras.Configuration
  implicit val config: Configuration = Configuration.default


}
