package com.example.books.infrastructure.codecs

import com.example.books.domain._
import com.example.shared.infrastucture.circe.CirceDefaults

trait BookCodecs extends CirceDefaults {
  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val BookIdCodec: Codec[BookId] = deriveUnwrappedCodec
  implicit val AuthorCodec: Codec[Author] = deriveUnwrappedCodec

  implicit val BookCodec: Codec[Book] = deriveConfiguredCodec
}
