package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.books.domain.publisher._
import com.example.books.infrastructure.codecs.PublisherCodecs
import com.example.shared.domain.page.PageResponse
import com.example.shared.domain.shared.IdMother
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

import java.util.UUID

class PublisherApiTest extends HasHttp4sRoutesSuite with PublisherCodecs {

  override val routes: HttpRoutes[IO] = module.publisherApi.routes

  val publisher: Publisher = PublisherMother.random
  val publisherId: UUID    = publisher.id.value

  test(POST(publisher, uri"publishers")).alias("CREATE") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(GET(uri"publishers" / s"$publisherId")).alias("FOUND") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Publisher], publisher)
  }

  lazy val notfoundId: UUID = IdMother.random.value

  test(GET(uri"publishers" / s"$notfoundId")).alias("NOT_FOUND") { response =>
    assertEquals(response.status, Status.NotFound)
    assertIO(response.as[Fail.NotFound], Fail.NotFound(s"Publisher for id: $notfoundId Not Found"))
  }

  test(GET(uri"publishers")).alias("LIST COMMON") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[PageResponse[Publisher]].map(_.elements.contains(publisher)), true)
  }

  test(GET(uri"publishers".withQueryParams(Map("filter" -> publisher.name.value)))).alias("LIST WITH FILTERS") {
    response =>
      assertEquals(response.status, Status.Ok)
      assertIO(
        response.as[PageResponse[Publisher]].map(_.elements.filter(_.name == publisher.name).contains(publisher)),
        true
      )
  }

  test(GET(uri"publishers?sort=-url")).alias("LIST WITH SORT") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Publisher]].map(_.elements.map(_.url.value)),
      response.as[PageResponse[Publisher]].map(_.elements.map(_.url.value)).unsafeRunSync().sorted.reverse
    )
  }

  lazy val updatedPublisher: Publisher = PublisherMother.random

  test(PUT(updatedPublisher, uri"publishers" / s"$publisherId")).alias("UPDATE") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(GET(uri"publishers" / s"$publisherId")).alias("UPDATED") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Publisher], updatedPublisher.copy(id = publisher.id))
  }

  test(DELETE(uri"publishers" / s"$publisherId")).alias("EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"publishers" / s"$notfoundId")).alias("NOT EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

}
