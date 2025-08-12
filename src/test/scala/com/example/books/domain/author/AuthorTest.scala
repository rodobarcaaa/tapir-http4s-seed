package com.example.books.domain.author

import munit.FunSuite
import com.example.shared.domain.common.Name
import com.example.shared.domain.shared.IdMother

class AuthorTest extends FunSuite {

  test("Author validation should pass for valid names") {
    val validAuthor = Author(IdMother.random, Name("John"), Name("Doe"))
    assert(validAuthor.validated.isValid)
  }

  test("Author validation should fail for empty first name") {
    val invalidAuthor = Author(IdMother.random, Name(""), Name("Doe"))
    assert(invalidAuthor.validated.isInvalid)
  }

  test("Author validation should fail for empty last name") {
    val invalidAuthor = Author(IdMother.random, Name("John"), Name(""))
    assert(invalidAuthor.validated.isInvalid)
  }

  test("Author validation should fail for both empty names") {
    val invalidAuthor = Author(IdMother.random, Name(""), Name(""))
    assert(invalidAuthor.validated.isInvalid)
  }

  test("Author completeName should combine first and last name") {
    val author = Author(IdMother.random, Name("John"), Name("Doe"))
    // The completeName method uses string interpolation, so it will call toString on Name objects
    assert(author.completeName.contains("John"))
    assert(author.completeName.contains("Doe"))
  }

  test("Author apply should create Author from tuple") {
    val id     = IdMother.random
    val author = Author.apply(id.value, "John", "Doe")
    assertEquals(author.id, id)
    assertEquals(author.firstName.value, "John")
    assertEquals(author.lastName.value, "Doe")
  }

  test("Author unapply should extract tuple from Author") {
    val author = AuthorMother.random
    val result = Author.unapply.apply(author)
    assert(result.isDefined)
    result.foreach { case (id, firstName, lastName) =>
      assertEquals(id, author.id.value)
      assertEquals(firstName, author.firstName.value)
      assertEquals(lastName, author.lastName.value)
    }
  }

  test("AuthorMother should create valid authors") {
    val author = AuthorMother.random
    assert(author.validated.isValid)
    assert(author.firstName.value.nonEmpty)
    assert(author.lastName.value.nonEmpty)
    assert(author.completeName.contains(" "))
  }
}
