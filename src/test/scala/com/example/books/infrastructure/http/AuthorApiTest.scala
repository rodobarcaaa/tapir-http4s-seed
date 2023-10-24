package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.books.domain.author._
import com.example.books.infrastructure.codecs.AuthorCodecs
import com.example.shared.domain.page.PageResponse
import com.example.shared.domain.shared.IdMother
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

import java.util.UUID

class AuthorApiTest extends HasHttp4sRoutesSuite with AuthorCodecs {

  override val routes: HttpRoutes[IO] = module.authorApi.routes

  val author: Author = AuthorMother.random
  val authorId: UUID = author.id.value

  test(POST(author, uri"authors")).alias("CREATE") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(GET(uri"authors" / s"$authorId")).alias("FOUND") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Author], author)
  }

  lazy val notfoundId: UUID = IdMother.random.value

  test(GET(uri"authors" / s"$notfoundId")).alias("NOT_FOUND") { response =>
    assertEquals(response.status, Status.NotFound)
    assertIO(response.as[Fail.NotFound], Fail.NotFound(s"Author for id: $notfoundId Not Found"))
  }

  test(GET(uri"authors")).alias("LIST") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[PageResponse[Author]].map(_.elements.contains(author)), true)
  }

  lazy val updatedAuthor: Author = AuthorMother.random

  test(PUT(updatedAuthor, uri"authors" / s"$authorId")).alias("UPDATE") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(GET(uri"authors" / s"$authorId")).alias("UPDATED") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Author], updatedAuthor.copy(id = author.id))
  }

  test(DELETE(uri"authors" / s"$authorId")).alias("EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"authors" / s"$notfoundId")).alias("NOT EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

}
