package com.example.books.infrastructure.codecs

import com.example.books.domain.book._
import com.example.shared.infrastructure.circe.CommonCodecs

trait BookCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val BookTitleCodec: Codec[BookTitle]             = deriveUnwrappedCodec
  implicit val BookIsbnCodec: Codec[BookIsbn]               = deriveUnwrappedCodec
  implicit val BookYearCodec: Codec[BookYear]               = deriveUnwrappedCodec
  implicit val BookDescriptionCodec: Codec[BookDescription] = deriveUnwrappedCodec

  implicit val BookCodec: Codec[Book] = deriveConfiguredCodec
}
