package com.example.shared.infrastructure.circe

import com.example.shared.domain.common.{Id, Name, URL}
import com.example.shared.domain.page.PageResponse

trait CommonCodecs extends CirceDefaults {
  import io.circe._
  import io.circe.generic.semiauto._
  import sttp.tapir.Schema

  // Manual codecs for value classes
  implicit val IdCodec: Codec[Id] = Codec.from(
    Decoder[java.util.UUID].map(Id.apply),
    Encoder[java.util.UUID].contramap(_.value)
  )

  implicit val NameCodec: Codec[Name] = Codec.from(
    Decoder[String].map(Name.apply),
    Encoder[String].contramap(_.value)
  )

  implicit val UrlCodec: Codec[URL] = Codec.from(
    Decoder[String].map(URL.apply),
    Encoder[String].contramap(_.value)
  )

  // Tapir schemas for value classes
  implicit val IdSchema: Schema[Id]     =
    Schema.string.map((uuid: String) => Some(Id(java.util.UUID.fromString(uuid))))(_.value.toString)
  implicit val NameSchema: Schema[Name] = Schema.string.map((str: String) => Some(Name(str)))(_.value)
  implicit val UrlSchema: Schema[URL]   = Schema.string.map((str: String) => Some(URL(str)))(_.value)

  implicit def prCodec[T](implicit c: Codec[T]): Codec[PageResponse[T]] = {
    implicitly[Codec[T]]
    deriveCodec[PageResponse[T]]
  }
}
