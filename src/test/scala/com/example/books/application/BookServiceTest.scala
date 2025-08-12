package com.example.books.application

import cats.effect.IO
import com.example.MainModule
import com.example.books.domain.author.AuthorMother
import com.example.books.domain.book.{Book, BookDescription, BookFilters, BookIsbn, BookMother, BookTitle, BookYear}
import com.example.books.domain.publisher.PublisherMother
import com.example.global.infrastructure.slick.Fly4sModule
import com.example.shared.domain.common.Id
import com.example.shared.domain.page.PageRequest
import com.example.shared.domain.shared.{IdMother, TextMother}
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class BookServiceTest extends CatsEffectSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Fly4sModule.migrateDbResource.use(_ => IO.unit).unsafeRunSync()
  }

  lazy val module: MainModule       = MainModule.initialize
  private lazy val bookService      = module.bookService
  private lazy val authorService    = module.authorService
  private lazy val publisherService = module.publisherService

  test("create should create a new book successfully") {
    val author    = AuthorMother.random
    val publisher = PublisherMother.random
    for {
      _     <- authorService.create(author)
      _     <- publisherService.create(publisher)
      book   = BookMother.random(author.id, publisher.id)
      _     <- bookService.create(book)
      found <- bookService.find(book.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundBook =>
        assertEquals(foundBook.id, book.id)
        assertEquals(foundBook.title, book.title)
        assertEquals(foundBook.isbn, book.isbn)
        assertEquals(foundBook.authorId, author.id)
        assertEquals(foundBook.publisherId, publisher.id)
      }
    }
  }

  test("create should fail with invalid book data") {
    val author    = AuthorMother.random
    val publisher = PublisherMother.random
    for {
      _          <- authorService.create(author)
      _          <- publisherService.create(publisher)
      invalidBook = Book(
                      IdMother.random,
                      BookIsbn(""),        // Invalid empty ISBN
                      BookTitle(""),       // Invalid empty title
                      BookDescription(""), // Invalid empty description
                      BookYear(1800),      // Invalid year
                      publisher.id,
                      author.id
                    )
      result     <- bookService.create(invalidBook).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.UnprocessableEntity])
      }
    }
  }

  test("update should update an existing book") {
    val author    = AuthorMother.random
    val publisher = PublisherMother.random
    val uniqueId  = scala.util.Random.alphanumeric.take(8).mkString
    for {
      _           <- authorService.create(author)
      _           <- publisherService.create(publisher)
      originalBook = BookMother.random(author.id, publisher.id)
      _           <- bookService.create(originalBook)
      updatedBook  = originalBook.copy(title = BookTitle(s"Updated Title $uniqueId"))
      _           <- bookService.update(originalBook.id, updatedBook)
      found       <- bookService.find(originalBook.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundBook =>
        assertEquals(foundBook.id, originalBook.id)
        assertEquals(foundBook.title.value, s"Updated Title $uniqueId")
        assertEquals(foundBook.isbn, originalBook.isbn)
      }
    }
  }

  test("find should return None for non-existing book") {
    val nonExistentId = IdMother.random
    for {
      found <- bookService.find(nonExistentId)
    } yield {
      assert(found.isEmpty)
    }
  }

  test("list should return paginated books") {
    val author    = AuthorMother.random
    val publisher = PublisherMother.random
    val uniqueId  = scala.util.Random.alphanumeric.take(8).mkString
    for {
      _      <- authorService.create(author)
      _      <- publisherService.create(publisher)
      book1   = BookMother(author.id, publisher.id, title = BookTitle(s"TestBook1_$uniqueId"))
      book2   = BookMother(author.id, publisher.id, title = BookTitle(s"TestBook2_$uniqueId"))
      _      <- bookService.create(book1)
      _      <- bookService.create(book2)
      pageRequest = PageRequest(1, 100) // Use larger page size to capture books in test environment
      result <- bookService.list(pageRequest, BookFilters.empty)
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == book1.id))
      assert(result.elements.exists(_.id == book2.id))
      // Also verify by title
      assert(result.elements.exists(_.title.value == s"TestBook1_$uniqueId"))
      assert(result.elements.exists(_.title.value == s"TestBook2_$uniqueId"))
    }
  }

  test("list should filter books by title") {
    val specificTitle = s"Specific Book Title ${scala.util.Random.alphanumeric.take(5).mkString}"
    val author        = AuthorMother.random
    val publisher     = PublisherMother.random
    for {
      _          <- authorService.create(author)
      _          <- publisherService.create(publisher)
      book1       = BookMother(author.id, publisher.id, title = BookTitle(specificTitle))
      book2       = BookMother.random(author.id, publisher.id)
      _          <- bookService.create(book1)
      _          <- bookService.create(book2)
      pageRequest = PageRequest(1, 10)
      filters     = BookFilters(filter = Some(specificTitle))
      result     <- bookService.list(pageRequest, filters)
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == book1.id))
      assert(!result.elements.exists(_.id == book2.id))
    }
  }

  test("delete should remove an existing book") {
    val author    = AuthorMother.random
    val publisher = PublisherMother.random
    for {
      _           <- authorService.create(author)
      _           <- publisherService.create(publisher)
      book         = BookMother.random(author.id, publisher.id)
      _           <- bookService.create(book)
      foundBefore <- bookService.find(book.id)
      _           <- bookService.delete(book.id)
      foundAfter  <- bookService.find(book.id)
    } yield {
      assert(foundBefore.isDefined)
      assert(foundAfter.isEmpty)
    }
  }

  test("delete should complete successfully even for non-existing book") {
    val nonExistentId = IdMother.random
    for {
      _ <- bookService.delete(nonExistentId)
    } yield {
      // Should complete without error
    }
  }
}
