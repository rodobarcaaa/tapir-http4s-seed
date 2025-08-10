package com.example.books.domain.publisher

import munit.FunSuite
import com.example.shared.domain.common.{Name, URL}
import com.example.shared.domain.shared.IdMother

class PublisherTest extends FunSuite {

  test("Publisher validation should pass for valid name and URL") {
    val validPublisher = Publisher(IdMother.random, Name("Penguin Books"), URL("www.penguin.com"))
    assert(validPublisher.validated.isValid)
  }

  test("Publisher validation should fail for empty name") {
    val invalidPublisher = Publisher(IdMother.random, Name(""), URL("www.penguin.com"))
    assert(invalidPublisher.validated.isInvalid)
  }

  test("Publisher validation should fail for empty URL") {
    val invalidPublisher = Publisher(IdMother.random, Name("Penguin Books"), URL(""))
    assert(invalidPublisher.validated.isInvalid)
  }

  test("Publisher validation should fail for both empty fields") {
    val invalidPublisher = Publisher(IdMother.random, Name(""), URL(""))
    assert(invalidPublisher.validated.isInvalid)
  }

  test("Publisher apply should create Publisher from tuple") {
    val id = IdMother.random
    val publisher = Publisher.apply(id.value, "Penguin Books", "www.penguin.com")
    assertEquals(publisher.id, id)
    assertEquals(publisher.name.value, "Penguin Books")
    assertEquals(publisher.url.value, "www.penguin.com")
  }

  test("Publisher unapply should extract tuple from Publisher") {
    val publisher = PublisherMother.random
    val result = Publisher.unapply.apply(publisher)
    assert(result.isDefined)
    result.foreach { case (id, name, url) =>
      assertEquals(id, publisher.id.value)
      assertEquals(name, publisher.name.value)
      assertEquals(url, publisher.url.value)
    }
  }

  test("PublisherMother should create valid publishers") {
    val publisher = PublisherMother.random
    assert(publisher.validated.isValid)
    assert(publisher.name.value.nonEmpty)
    assert(publisher.url.value.nonEmpty)
  }
}