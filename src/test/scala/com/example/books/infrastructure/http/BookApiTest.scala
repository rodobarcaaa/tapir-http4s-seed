package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.books.domain.book._
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.books.infrastructure.helpers._
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.PageResponse
import com.example.shared.domain.shared.IdMother
import com.example.shared.infrastructure.http.{Fail, HasHttp4sRoutesSuite}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

import java.util.UUID

class BookApiTest extends HasHttp4sRoutesSuite with BookCodecs with AuthorHelper with PublisherHelper {

  override val routes: HttpRoutes[IO] = module.bookApi.routes

  val authorId: Id    = createRandomAuthor
  val publisherId: Id = createRandomPublisher
  val book: Book      = BookMother.random(authorId, publisherId)
  val bookId: UUID    = book.id.value

  test(POST(book, uri"books")).alias("CREATE") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(POST(BookMother.random(IdMother.random, publisherId), uri"books")).alias("CONFLICT Author") { response =>
    assertEquals(response.status, Status.Conflict)
  }

  test(POST(BookMother.random(authorId, IdMother.random), uri"books")).alias("CONFLICT Publisher") { response =>
    assertEquals(response.status, Status.Conflict)
  }

  test(GET(uri"books" / s"$bookId")).alias("FOUND") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Book], book)
  }

  lazy val notfoundId: UUID = IdMother.random.value

  test(GET(uri"books" / s"$notfoundId")).alias("NOT_FOUND") { response =>
    assertEquals(response.status, Status.NotFound)
    assertIO(response.as[Fail.NotFound], Fail.NotFound(s"Book for id: $notfoundId Not Found"))
  }

  test(GET(uri"books")).alias("LIST COMMON") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[PageResponse[Book]].map(_.elements.contains(book)), true)
  }

  test(GET(uri"books".withQueryParams(Map("filter" -> book.title.value)))).alias("LIST WITH FILTERS") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Book]].map(_.elements.filter(_.title == book.title).contains(book)),
      true
    )
  }

  test(GET(uri"books".withQueryParams(Map("isbn" -> book.isbn.value)))).alias("LIST WITH FILTERS (isbn)") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Book]].map(_.elements.filter(_.isbn == book.isbn).contains(book)),
      true
    )
  }

  test(GET(uri"books".withQueryParams(Map("year" -> book.year.value.toString)))).alias("LIST WITH FILTERS (year)") {
    response =>
      assertEquals(response.status, Status.Ok)
      assertIO(
        response.as[PageResponse[Book]].map(_.elements.filter(_.year == book.year).contains(book)),
        true
      )
  }

  test(GET(uri"books?sort=-isbn")).alias("LIST WITH SORT (isbn)") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Book]].map(_.elements.map(_.isbn.value)),
      response.as[PageResponse[Book]].map(_.elements.map(_.isbn.value)).unsafeRunSync().sorted.reverse
    )
  }

  test(GET(uri"books?sort=year")).alias("LIST WITH SORT (year)") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(
      response.as[PageResponse[Book]].map(_.elements.map(_.year.value)),
      response.as[PageResponse[Book]].map(_.elements.map(_.year.value)).unsafeRunSync().sorted
    )
  }

  lazy val updatedBook: Book = BookMother.random(authorId, publisherId)

  test(PUT(updatedBook, uri"books" / s"$bookId")).alias("UPDATE") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(GET(uri"books" / s"$bookId")).alias("UPDATED") { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Book], updatedBook.copy(id = book.id))
  }

  test(DELETE(uri"books" / s"$bookId")).alias("EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"books" / s"$notfoundId")).alias("NOT EXISTS") { response =>
    assertEquals(response.status, Status.NoContent)
  }

}
