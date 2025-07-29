package com.example.books.infrastructure.codecs

import com.example.books.domain.author.Author
import com.example.shared.infrastructure.circe.CommonCodecs

trait AuthorCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val AuthorCodec: Codec[Author] = deriveCodec
}
