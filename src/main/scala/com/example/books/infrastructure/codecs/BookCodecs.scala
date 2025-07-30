package com.example.books.infrastructure.codecs

import com.example.books.domain.book._
import com.example.shared.infrastructure.circe.CommonCodecs

trait BookCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.semiauto._
  import sttp.tapir.Schema

  // Manual codecs for value classes
  implicit val BookTitleCodec: Codec[BookTitle] = Codec.from(
    Decoder[String].map(BookTitle.apply),
    Encoder[String].contramap(_.value)
  )

  implicit val BookIsbnCodec: Codec[BookIsbn] = Codec.from(
    Decoder[String].map(BookIsbn.apply),
    Encoder[String].contramap(_.value)
  )

  implicit val BookYearCodec: Codec[BookYear] = Codec.from(
    Decoder[Int].map(BookYear.apply),
    Encoder[Int].contramap(_.value)
  )

  implicit val BookDescriptionCodec: Codec[BookDescription] = Codec.from(
    Decoder[String].map(BookDescription.apply),
    Encoder[String].contramap(_.value)
  )

  // Tapir schemas for value classes
  implicit val BookTitleSchema: Schema[BookTitle]             = Schema.string.map((str: String) => Some(BookTitle(str)))(_.value)
  implicit val BookIsbnSchema: Schema[BookIsbn]               = Schema.string.map((str: String) => Some(BookIsbn(str)))(_.value)
  implicit val BookYearSchema: Schema[BookYear]               = Schema.schemaForInt.map((int: Int) => Some(BookYear(int)))(_.value)
  implicit val BookDescriptionSchema: Schema[BookDescription] =
    Schema.string.map((str: String) => Some(BookDescription(str)))(_.value)

  implicit val BookCodec: Codec[Book] = deriveCodec
}
