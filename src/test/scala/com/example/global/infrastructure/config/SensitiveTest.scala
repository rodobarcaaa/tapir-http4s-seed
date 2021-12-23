package com.example.global.infrastructure.config

import munit.FunSuite

class SensitiveTest extends FunSuite {
  test("password sensitive field not show value") {
    val password = Sensitive("password1")
    assertEquals(password.toString, "***")
    assertNotEquals(password.toString, password.value)
  }
}
