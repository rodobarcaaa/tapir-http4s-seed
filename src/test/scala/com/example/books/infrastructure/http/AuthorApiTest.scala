package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.books.domain.author._
import com.example.books.infrastructure.codecs.AuthorCodecs
import com.example.books.infrastructure.helpers.AuthHelper
import com.example.shared.domain.page.PageResponse
import com.example.shared.domain.shared.IdMother
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

import java.util.UUID

class AuthorApiTest extends HasHttp4sRoutesSuite with AuthorCodecs with AuthHelper {

  override val routes: HttpRoutes[IO] = module.authorApi.routes

  val author: Author = AuthorMother.random
  val authorId: UUID = author.id.value

  test(POST(author, uri"authors").withHeaders(adminAuthHeader)).alias("CREATE") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(GET(uri"authors" / s"$authorId").withHeaders(defaultAuthHeader)).alias("FOUND") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Author], author)
  }

  lazy val notfoundId: UUID = IdMother.random.value

  test(GET(uri"authors" / s"$notfoundId").withHeaders(defaultAuthHeader)).alias("NOT_FOUND") { response =>
    assertEquals(response.status, Status.NotFound)
    assertIO(response.as[Fail.NotFound], Fail.NotFound(s"Author for id: $notfoundId Not Found"))
  }

  test(GET(uri"authors").withHeaders(defaultAuthHeader)).alias("LIST COMMON") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[PageResponse[Author]].map(_.elements.contains(author)), true)
  }

  test(GET(uri"authors".withQueryParams(Map("filter" -> author.firstName.value))).withHeaders(defaultAuthHeader))
    .alias("LIST WITH FILTERS") { response =>
      assertEquals(response.status, Status.Ok)
      assertIO(
        response.as[PageResponse[Author]].map(_.elements.filter(_.firstName == author.firstName).contains(author)),
        true
      )
    }

  test(GET(uri"authors?sort=lastName").withHeaders(defaultAuthHeader)).alias("LIST WITH SORT") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Author]].map(_.elements.map(_.lastName.value.toUpperCase)),
      response.as[PageResponse[Author]].map(_.elements.map(_.lastName.value.toUpperCase)).unsafeRunSync().sorted
    )
  }

  lazy val updatedAuthor: Author = AuthorMother.random

  test(PUT(updatedAuthor, uri"authors" / s"$authorId").withHeaders(adminAuthHeader)).alias("UPDATE") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(GET(uri"authors" / s"$authorId").withHeaders(defaultAuthHeader)).alias("UPDATED") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Author], updatedAuthor.copy(id = author.id))
  }

  test(DELETE(uri"authors" / s"$authorId").withHeaders(adminAuthHeader)).alias("EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"authors" / s"$notfoundId").withHeaders(adminAuthHeader)).alias("NOT EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

}
