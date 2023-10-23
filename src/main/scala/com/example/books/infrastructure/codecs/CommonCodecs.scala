package com.example.books.infrastructure.codecs

import com.example.books.domain.common.{Id, Name, URL}
import com.example.shared.domain.page.PageResponse
import com.example.shared.infrastructure.circe.CirceDefaults

trait CommonCodecs extends CirceDefaults {
  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val IdCodec: Codec[Id]     = deriveUnwrappedCodec
  implicit val NameCodec: Codec[Name] = deriveUnwrappedCodec
  implicit val UrlCodec: Codec[URL]   = deriveUnwrappedCodec

  implicit def prCodec[T](implicit c: Codec[T]): Codec[PageResponse[T]] = {
    implicitly[Codec[T]]
    deriveConfiguredCodec[PageResponse[T]]
  }
}
