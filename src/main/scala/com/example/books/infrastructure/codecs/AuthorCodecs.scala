package com.example.books.infrastructure.codecs

import com.example.books.domain.author.Author

trait AuthorCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val AuthorCodec: Codec[Author] = deriveConfiguredCodec
}
