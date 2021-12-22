package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.MainModule
import com.example.books.domain.{Author, Book, BookId}
import com.example.books.infrastructure.codecs.BookCodecs
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.dsl.io._
import org.http4s.dsl.io._
import org.http4s.implicits._

import java.util.UUID

class BookApiTest extends munit.Http4sHttpRoutesSuite with BookCodecs {
  lazy val module: MainModule = MainModule.initialize

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
