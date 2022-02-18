package com.example.books.infrastructure.http

import com.example.books.application.BookService
import com.example.books.domain._
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.infrastructure.http._

import java.util.UUID

class BookApi(bookService: BookService) extends HasTapirResource with BookCodecs {

  private val service    = "Books"
  private val base       = baseEndpoint.tag(service).in(service.toLowerCase)
  private val baseWithId = base.in(path[UUID]("id"))

  private val post = base.post
    .in(jsonBody[Book])
    .out(jsonBody[BookId])
    .out(statusCode(Created))
    .serverLogicSuccess { book =>
      bookService.create(book)
    }

  private val list = base.get.out(jsonBody[List[Book]]).serverLogicSuccess { _ =>
    bookService.list
  }

  private val get = baseWithId.get
    .out(jsonBody[Book])
    .serverLogic { id =>
      bookService.find(BookId(id)).map(_.toRight(Fail.NotFound(s"Book for id: $id Not Found"): Fail))
    }

  private val put = baseWithId.put
    .in(jsonBody[Book])
    .out(statusCode(NoContent))
    .serverLogicSuccess { case (id, book) =>
      bookService.update(BookId(id), book)
    }

  private val delete = baseWithId.delete
    .out(statusCode(NoContent))
    .serverLogicSuccess { id =>
      bookService.delete(BookId(id))
    }

  override val endpoints: ServerEndpoints = List(post, list, get, put, delete)
}
