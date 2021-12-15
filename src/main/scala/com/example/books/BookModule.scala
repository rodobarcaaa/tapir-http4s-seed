package com.example.books

import com.example.books.application.BookService
import com.example.books.domain.BookRepository
import com.example.books.infrastructure.http.BookApi
import com.example.books.infrastructure.repository.MemoryBookRepository

trait BookModule {
  import com.softwaremill.macwire._

  lazy val bookRepository: BookRepository = wire[MemoryBookRepository]
  lazy val bookService: BookService       = wire[BookService]
  lazy val bookApi: BookApi               = wire[BookApi]
}
