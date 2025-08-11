package com.example.shared.domain.common

import munit.FunSuite

class HasValidationsTest extends FunSuite {

  test("validateEmpty should pass for non-empty string") {
    val validation = HasValidations.validateEmpty("test", "field")
    assert(validation.isValid)
  }

  test("validateEmpty should fail for empty string") {
    val validation = HasValidations.validateEmpty("", "field")
    assert(validation.isInvalid)
    validation.fold(
      errors => assert(errors.exists(_.contains("field should not be empty"))),
      _ => fail("Expected validation to fail")
    )
  }

  test("validateEmpty should use custom tag in error message") {
    val validation = HasValidations.validateEmpty("", "customField")
    assert(validation.isInvalid)
    validation.fold(
      errors => assert(errors.exists(_.contains("customField should not be empty"))),
      _ => fail("Expected validation to fail")
    )
  }

  test("validateEmpty should pass for string with whitespace") {
    val validation = HasValidations.validateEmpty("  ", "field")
    assert(validation.isValid)
  }
}
