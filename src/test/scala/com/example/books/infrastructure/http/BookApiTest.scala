package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.books.domain.{Author, Book, BookId}
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.implicits._

import java.util.UUID

class BookApiTest extends HasHttp4sRoutesSuite with BookCodecs {

  override val routes: HttpRoutes[IO] = module.bookApi.routes

  private val books = module.bookRepository.books.get()

  private val book: Book = Book(
    BookId(UUID.randomUUID),
    "The Pragmatic Programmer, 20th Anniversary Edition",
    2019,
    Author("David Thomas, Andrew Hunt")
  )

  test(GET(uri"books" / s"${books.head.id.value}")) { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[Book], books.head)
  }

  test(GET(uri"books")) { response =>
    assertEquals(response.status, Status.Ok)
    assertIO(response.as[List[Book]], books)
  }

  test(POST(book, uri"books")) { response =>
    assertEquals(response.status, Status.Created)
  }

  test(PUT(book, uri"books" / s"${books.head.id.value}")) { response =>
    assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"books" / s"${books.head.id.value}")) { response =>
    assertEquals(response.status, Status.NoContent)
  }

}
