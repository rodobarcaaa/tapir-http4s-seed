package com.example.books

import com.example.books.application._
import com.example.books.domain.author.AuthorRepository
import com.example.books.domain.book.BookRepository
import com.example.books.domain.publisher.PublisherRepository
import com.example.books.infrastructure.http._
import com.example.books.infrastructure.repository._

trait BookModule {
  import com.softwaremill.macwire._

  lazy val authorRepository: AuthorRepository = wire[SlickAuthorRepository]
  lazy val authorService: AuthorService       = wire[AuthorService]
  lazy val authorApi: AuthorApi               = wire[AuthorApi]

  lazy val publisherRepository: PublisherRepository = wire[SlickPublisherRepository]
  lazy val publisherService: PublisherService       = wire[PublisherService]
  lazy val publisherApi: PublisherApi               = wire[PublisherApi]

  lazy val bookRepository: BookRepository = wire[SlickBookRepository]
  lazy val bookService: BookService       = wire[BookService]
  lazy val bookApi: BookApi               = wire[BookApi]
}
