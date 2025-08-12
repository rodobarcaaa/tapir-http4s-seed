package com.example.books.application

import cats.effect.IO
import com.example.MainModule
import com.example.books.domain.author.{Author, AuthorMother}
import com.example.global.infrastructure.slick.Fly4sModule
import com.example.shared.domain.common.{Id, Name}
import com.example.shared.domain.page.PageRequest
import com.example.shared.domain.shared.{IdMother, TextMother}
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class AuthorServiceTest extends CatsEffectSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Fly4sModule.migrateDbResource.use(_ => IO.unit).unsafeRunSync()
  }

  lazy val module: MainModule    = MainModule.initialize
  private lazy val authorService = module.authorService

  test("create should create a new author successfully") {
    val author = AuthorMother.random
    for {
      _     <- authorService.create(author)
      found <- authorService.find(author.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundAuthor =>
        assertEquals(foundAuthor.id, author.id)
        assertEquals(foundAuthor.firstName, author.firstName)
        assertEquals(foundAuthor.lastName, author.lastName)
      }
    }
  }

  test("create should fail with invalid author data") {
    val invalidAuthor = Author(IdMother.random, Name(""), Name(""))
    for {
      result <- authorService.create(invalidAuthor).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.UnprocessableEntity])
      }
    }
  }

  test("update should update an existing author") {
    val originalAuthor = AuthorMother.random
    val updatedAuthor  = originalAuthor.copy(firstName = Name("UpdatedFirstName"))
    for {
      _     <- authorService.create(originalAuthor)
      _     <- authorService.update(originalAuthor.id, updatedAuthor)
      found <- authorService.find(originalAuthor.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundAuthor =>
        assertEquals(foundAuthor.id, originalAuthor.id)
        assertEquals(foundAuthor.firstName.value, "UpdatedFirstName")
        assertEquals(foundAuthor.lastName, originalAuthor.lastName)
      }
    }
  }

  test("find should return None for non-existing author") {
    val nonExistentId = IdMother.random
    for {
      found <- authorService.find(nonExistentId)
    } yield {
      assert(found.isEmpty)
    }
  }

  test("list should return paginated authors") {
    val uniqueId    = scala.util.Random.alphanumeric.take(8).mkString
    val author1     = AuthorMother(firstName = Name(s"TestAuthor1_$uniqueId"))
    val author2     = AuthorMother(firstName = Name(s"TestAuthor2_$uniqueId"))
    val pageRequest = PageRequest(1, 100) // Use larger page size to capture authors in test environment
    for {
      _      <- authorService.create(author1)
      _      <- authorService.create(author2)
      result <- authorService.list(pageRequest, None)
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == author1.id))
      assert(result.elements.exists(_.id == author2.id))
      // Also verify that the authors can be found by their unique names
      assert(result.elements.exists(_.firstName.value == s"TestAuthor1_$uniqueId"))
      assert(result.elements.exists(_.firstName.value == s"TestAuthor2_$uniqueId"))
    }
  }

  test("list should filter authors by name") {
    val specificName = s"SpecificName${scala.util.Random.alphanumeric.take(5).mkString}"
    val author1      = AuthorMother(firstName = Name(specificName))
    val author2      = AuthorMother.random
    val pageRequest  = PageRequest(1, 10)
    for {
      _      <- authorService.create(author1)
      _      <- authorService.create(author2)
      result <- authorService.list(pageRequest, Some(specificName))
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == author1.id))
      assert(!result.elements.exists(_.id == author2.id))
    }
  }

  test("delete should remove an existing author") {
    val author = AuthorMother.random
    for {
      _           <- authorService.create(author)
      foundBefore <- authorService.find(author.id)
      _           <- authorService.delete(author.id)
      foundAfter  <- authorService.find(author.id)
    } yield {
      assert(foundBefore.isDefined)
      assert(foundAfter.isEmpty)
    }
  }

  test("delete should complete successfully even for non-existing author") {
    val nonExistentId = IdMother.random
    for {
      _ <- authorService.delete(nonExistentId)
    } yield {
      // Should complete without error
    }
  }
}
