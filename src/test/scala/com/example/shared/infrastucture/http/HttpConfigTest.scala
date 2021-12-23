package com.example.shared.infrastucture.http

import munit.FunSuite

class HttpConfigTest extends FunSuite {
  test("http config toUrl") {
    val config = HttpConfig("0.0.0.0", 8080)
    assertEquals(config.toUrl("https://"), "https://0.0.0.0:8080")
  }
}
