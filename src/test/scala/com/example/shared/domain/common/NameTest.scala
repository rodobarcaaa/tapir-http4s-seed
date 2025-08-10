package com.example.shared.domain.common

import munit.FunSuite

class NameTest extends FunSuite {

  test("Name validation should pass for valid name") {
    val validName = Name("John Doe")
    assert(validName.validate().isValid)
  }

  test("Name validation should fail for empty name") {
    val emptyName = Name("")
    assert(emptyName.validate().isInvalid)
  }

  test("Name validation should fail for name too long") {
    val longName = Name("A" * 300) // exceeds Name.maxLength (255)
    assert(longName.validate().isInvalid)
  }

  test("Name validation should accept custom tag") {
    val invalidName = Name("")
    val validation = invalidName.validate("firstName")
    assert(validation.isInvalid)
    // The error message should contain the custom tag
    validation.fold(
      errors => assert(errors.exists(_.contains("firstName"))),
      _ => fail("Expected validation to fail")
    )
  }

  test("Name validation should accept custom max length") {
    val name = Name("This is a longer name")
    val shortValidation = name.validate(maxLength = 10)
    val longValidation = name.validate(maxLength = 50)
    
    assert(shortValidation.isInvalid)
    assert(longValidation.isValid)
  }

  test("Name maxLength should be 255") {
    assertEquals(Name.maxLength, 255)
  }
}