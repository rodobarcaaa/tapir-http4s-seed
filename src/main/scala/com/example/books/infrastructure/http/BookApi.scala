package com.example.books.infrastructure.http

import cats.data.NonEmptyList
import com.example.books.application.BookService
import com.example.books.domain._
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.infrastucture.http.ServerEndpoints
import sttp.tapir._
import sttp.tapir.generic.auto._
import sttp.tapir.json.circe.jsonBody

import java.util.UUID

class BookApi(bookService: BookService) extends BookCodecs {

  private val service    = "Books"
  private val base       = endpoint.tag(service).in(service.toLowerCase)
  private val baseWithId = base.in(path[UUID]("id"))

  private val post = base.post.in(jsonBody[Book]).out(jsonBody[BookId]).serverLogicSuccess { book =>
    bookService.create(book)
  }

  private val list = base.get.out(jsonBody[List[Book]]).serverLogicSuccess { _ =>
    bookService.list
  }

  private val get = baseWithId.get.out(jsonBody[Book]).serverLogicSuccess { id =>
    bookService.find(BookId(id)).map(_.getOrElse(???))
  }

  private val put = baseWithId.put.in(jsonBody[Book]).serverLogicSuccess { case (id, book) =>
    bookService.update(BookId(id), book)
  }

  private val delete = baseWithId.delete.serverLogicSuccess { id =>
    bookService.delete(BookId(id))
  }

  val endpoints: ServerEndpoints = NonEmptyList.of(post, list, get, put, delete)
}
