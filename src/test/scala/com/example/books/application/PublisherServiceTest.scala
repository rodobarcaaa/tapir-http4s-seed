package com.example.books.application

import cats.effect.IO
import com.example.MainModule
import com.example.books.domain.publisher.{Publisher, PublisherMother}
import com.example.global.infrastructure.slick.Fly4sModule
import com.example.shared.domain.common.{Name, URL}
import com.example.shared.domain.page.PageRequest
import com.example.shared.domain.shared.{IdMother, TextMother}
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class PublisherServiceTest extends CatsEffectSuite {

  override def beforeAll(): Unit = {
    super.beforeAll()
    Fly4sModule.migrateDbResource.use(_ => IO.unit).unsafeRunSync()
  }

  lazy val module: MainModule = MainModule.initialize
  private lazy val publisherService = module.publisherService

  test("create should create a new publisher successfully") {
    val publisher = PublisherMother.random
    for {
      _ <- publisherService.create(publisher)
      found <- publisherService.find(publisher.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundPublisher =>
        assertEquals(foundPublisher.id, publisher.id)
        assertEquals(foundPublisher.name, publisher.name)
        assertEquals(foundPublisher.url, publisher.url)
      }
    }
  }

  test("create should fail with invalid publisher data") {
    val invalidPublisher = Publisher(IdMother.random, Name(""), URL(""))
    for {
      result <- publisherService.create(invalidPublisher).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.UnprocessableEntity])
      }
    }
  }

  test("update should update an existing publisher") {
    val originalPublisher = PublisherMother.random
    val updatedPublisher = originalPublisher.copy(name = Name("Updated Publisher Name"))
    for {
      _ <- publisherService.create(originalPublisher)
      _ <- publisherService.update(originalPublisher.id, updatedPublisher)
      found <- publisherService.find(originalPublisher.id)
    } yield {
      assert(found.isDefined)
      found.foreach { foundPublisher =>
        assertEquals(foundPublisher.id, originalPublisher.id)
        assertEquals(foundPublisher.name.value, "Updated Publisher Name")
        assertEquals(foundPublisher.url, originalPublisher.url)
      }
    }
  }

  test("find should return None for non-existing publisher") {
    val nonExistentId = IdMother.random
    for {
      found <- publisherService.find(nonExistentId)
    } yield {
      assert(found.isEmpty)
    }
  }

  test("list should return paginated publishers") {
    val publisher1 = PublisherMother.random
    val publisher2 = PublisherMother.random
    val pageRequest = PageRequest(1, 10)
    for {
      _ <- publisherService.create(publisher1)
      _ <- publisherService.create(publisher2)
      result <- publisherService.list(pageRequest, None)
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == publisher1.id))
      assert(result.elements.exists(_.id == publisher2.id))
    }
  }

  test("list should filter publishers by name") {
    val specificName = s"SpecificPub${scala.util.Random.alphanumeric.take(5).mkString}"
    val publisher1 = PublisherMother(name = Name(specificName))
    val publisher2 = PublisherMother.random
    val pageRequest = PageRequest(1, 10)
    for {
      _ <- publisherService.create(publisher1)
      _ <- publisherService.create(publisher2)
      result <- publisherService.list(pageRequest, Some(specificName))
    } yield {
      assert(result.elements.nonEmpty)
      assert(result.elements.exists(_.id == publisher1.id))
      assert(!result.elements.exists(_.id == publisher2.id))
    }
  }

  test("delete should remove an existing publisher") {
    val publisher = PublisherMother.random
    for {
      _ <- publisherService.create(publisher)
      foundBefore <- publisherService.find(publisher.id)
      _ <- publisherService.delete(publisher.id)
      foundAfter <- publisherService.find(publisher.id)
    } yield {
      assert(foundBefore.isDefined)
      assert(foundAfter.isEmpty)
    }
  }

  test("delete should complete successfully even for non-existing publisher") {
    val nonExistentId = IdMother.random
    for {
      _ <- publisherService.delete(nonExistentId)
    } yield {
      // Should complete without error
    }
  }
}