package com.example.shared.domain.common

import munit.FunSuite

class URLTest extends FunSuite {

  test("URL validation should pass for valid HTTP URL") {
    val validUrl = URL("http://www.example.com")
    assert(validUrl.validate().isValid)
  }

  test("URL validation should pass for valid HTTPS URL") {
    val validUrl = URL("https://www.example.com")
    assert(validUrl.validate().isValid)
  }

  test("URL validation should pass for URL without www") {
    val validUrl = URL("https://example.com")
    assert(validUrl.validate().isValid)
  }

  test("URL validation should pass for URL with path") {
    val validUrl = URL("https://www.example.com/path/to/resource")
    assert(validUrl.validate().isValid)
  }

  test("URL validation should pass for URL with query parameters") {
    val validUrl = URL("https://www.example.com/search?q=test&limit=10")
    assert(validUrl.validate().isValid)
  }

  test("URL validation should fail for empty URL") {
    val emptyUrl = URL("")
    assert(emptyUrl.validate().isInvalid)
  }

  test("URL validation should fail for invalid URL format") {
    val invalidUrl = URL("not-a-url")
    assert(invalidUrl.validate().isInvalid)
  }

  test("URL validation should pass for URL without protocol (actual regex behavior)") {
    val urlWithoutProtocol = URL("www.example.com")
    val validation         = urlWithoutProtocol.validate()
    assert(validation.isValid)
  }

  test("URL validation should accept custom tag") {
    val invalidUrl = URL("invalid-url")
    val validation = invalidUrl.validate("website")
    assert(validation.isInvalid)
    // The error message should contain the custom tag
    validation.fold(
      errors => assert(errors.exists(_.contains("website"))),
      _ => fail("Expected validation to fail")
    )
  }

  test("URL regexp should be accessible") {
    assert(URL.regexp.nonEmpty)
    assert(URL.regexp.contains("http"))
  }
}
