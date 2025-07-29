package com.example.shared.infrastructure.circe

import com.example.shared.domain.common.{Id, Name, URL}
import com.example.shared.domain.page.PageResponse

trait CommonCodecs extends CirceDefaults {
  import io.circe._
  import io.circe.generic.semiauto._

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

  implicit def prCodec[T](implicit c: Codec[T]): Codec[PageResponse[T]] = {
    implicitly[Codec[T]]
    deriveCodec[PageResponse[T]]
  }
}
