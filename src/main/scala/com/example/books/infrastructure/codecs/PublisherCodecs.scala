package com.example.books.infrastructure.codecs

import com.example.books.domain.publisher.Publisher

trait PublisherCodecs extends CommonCodecs {

  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val PublisherCodec: Codec[Publisher] = deriveConfiguredCodec
}
