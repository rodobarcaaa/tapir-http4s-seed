package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.MainModule
import com.example.books.domain.Book
import com.example.books.infrastructure.codecs.BookCodecs
import munit.CatsEffectSuite
import org.http4s._
import org.http4s.circe.CirceSensitiveDataEntityDecoder.circeEntityDecoder
import org.http4s.implicits._

class BookApiTest extends CatsEffectSuite with BookCodecs {

  lazy val module: MainModule = MainModule.initialize

  test("Books returns status code 200") {
    lazy val books: Request[IO] = Request(method = Method.GET, uri = uri"/books")
    lazy val retBooks           = module.httpApi.apiRoutes.orNotFound(books)
    assertIO(retBooks.map(_.status), Status.Ok)
  }

}
