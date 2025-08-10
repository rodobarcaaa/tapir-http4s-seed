package com.example.books.domain.book

import munit.FunSuite
import com.example.shared.domain.common.Id
import com.example.shared.domain.shared.IdMother
import java.time.LocalDate

class BookTest extends FunSuite {

  test("BookTitle validation should pass for valid title") {
    val validTitle = BookTitle("A Valid Book Title")
    assert(validTitle.validate().isValid)
  }

  test("BookTitle validation should fail for empty title") {
    val emptyTitle = BookTitle("")
    assert(emptyTitle.validate().isInvalid)
  }

  test("BookTitle validation should fail for title too long") {
    val longTitle = BookTitle("A" * 300) // exceeds maxLengthMediumText (255)
    assert(longTitle.validate().isInvalid)
  }

  test("BookIsbn validation should pass for valid ISBN") {
    val validIsbn = BookIsbn("978-0134685991")
    assert(validIsbn.validate().isValid)
  }

  test("BookIsbn validation should fail for empty ISBN") {
    val emptyIsbn = BookIsbn("")
    assert(emptyIsbn.validate().isInvalid)
  }

  test("BookIsbn validation should fail for ISBN too long") {
    val longIsbn = BookIsbn("A" * 50) // exceeds maxLengthSmallText (35)
    assert(longIsbn.validate().isInvalid)
  }

  test("BookDescription validation should pass for valid description") {
    val validDescription = BookDescription("A valid book description")
    assert(validDescription.validate().isValid)
  }

  test("BookDescription validation should fail for empty description") {
    val emptyDescription = BookDescription("")
    assert(emptyDescription.validate().isInvalid)
  }

  test("BookDescription validation should fail for description too long") {
    val longDescription = BookDescription("A" * 1300) // exceeds maxLengthLongText (1255)
    assert(longDescription.validate().isInvalid)
  }

  test("BookYear validation should pass for valid current year") {
    val currentYear = BookYear(LocalDate.now.getYear)
    assert(currentYear.validate().isValid)
  }

  test("BookYear validation should pass for valid past year") {
    val pastYear = BookYear(2020)
    assert(pastYear.validate().isValid)
  }

  test("BookYear validation should fail for year before 1900") {
    val oldYear = BookYear(1800)
    assert(oldYear.validate().isInvalid)
  }

  test("BookYear validation should fail for future year") {
    val futureYear = BookYear(LocalDate.now.getYear + 1)
    assert(futureYear.validate().isInvalid)
  }

  test("BookYear validation should fail for invalid year format") {
    val invalidYear = BookYear(20) // not 4 digits
    assert(invalidYear.validate().isInvalid)
  }

  test("Book validation should pass for all valid fields") {
    val validBook = Book(
      IdMother.random,
      BookIsbn("978-0134685991"),
      BookTitle("Clean Code"),
      BookDescription("A handbook of agile software craftsmanship"),
      BookYear(2008),
      IdMother.random,
      IdMother.random
    )
    assert(validBook.validated.isValid)
  }

  test("Book validation should fail for invalid fields") {
    val invalidBook = Book(
      IdMother.random,
      BookIsbn(""), // invalid
      BookTitle(""), // invalid
      BookDescription(""), // invalid
      BookYear(1800), // invalid
      IdMother.random,
      IdMother.random
    )
    assert(invalidBook.validated.isInvalid)
  }

  test("Book apply should create Book from tuple") {
    val id = IdMother.random
    val authorId = IdMother.random
    val publisherId = IdMother.random
    val book = Book.apply(
      id.value,
      "978-0134685991",
      "Clean Code",
      "A handbook of agile software craftsmanship",
      2008,
      publisherId.value,
      authorId.value
    )
    assertEquals(book.id, id)
    assertEquals(book.isbn.value, "978-0134685991")
    assertEquals(book.title.value, "Clean Code")
    assertEquals(book.description.value, "A handbook of agile software craftsmanship")
    assertEquals(book.year.value, 2008)
    assertEquals(book.publisherId, publisherId)
    assertEquals(book.authorId, authorId)
  }

  test("Book unapply should extract tuple from Book") {
    val book = BookMother.random(IdMother.random, IdMother.random)
    val result = Book.unapply.apply(book)
    assert(result.isDefined)
    result.foreach { case (id, isbn, title, description, year, publisherId, authorId) =>
      assertEquals(id, book.id.value)
      assertEquals(isbn, book.isbn.value)
      assertEquals(title, book.title.value)
      assertEquals(description, book.description.value)
      assertEquals(year, book.year.value)
      assertEquals(publisherId, book.publisherId.value)
      assertEquals(authorId, book.authorId.value)
    }
  }
}