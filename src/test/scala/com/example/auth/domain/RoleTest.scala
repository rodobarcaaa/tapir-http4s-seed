package com.example.auth.domain

import munit.FunSuite

class RoleTest extends FunSuite {

  test("Role.fromString should parse valid roles") {
    assertEquals(Role.fromString("admin"), Some(Role.Admin))
    assertEquals(Role.fromString("customer"), Some(Role.Customer))
    assertEquals(Role.fromString("ADMIN"), Some(Role.Admin))       // case insensitive
    assertEquals(Role.fromString("CUSTOMER"), Some(Role.Customer)) // case insensitive
    assertEquals(Role.fromString("invalid"), None)
  }

  test("Role.toString should convert roles to strings") {
    assertEquals(Role.toString(Role.Admin), "admin")
    assertEquals(Role.toString(Role.Customer), "customer")
  }

  test("User.create should default to Customer role") {
    val user = User.create("testuser", "test@example.com", "hashedpass")
    assertEquals(user.role, Role.Customer)
  }

  test("User.create should accept explicit role") {
    val user = User.create("admin", "admin@example.com", "hashedpass", Role.Admin)
    assertEquals(user.role, Role.Admin)
  }

  test("UserInfo.fromUser should include role") {
    val user     = User.create("testuser", "test@example.com", "hashedpass", Role.Admin)
    val userInfo = UserInfo.fromUser(user)
    assertEquals(userInfo.role, Role.Admin)
  }

  test("UserCreateRequest should handle optional role") {
    val requestWithoutRole = UserCreateRequest("user", "user@example.com", "pass")
    assertEquals(requestWithoutRole.role, None)

    val requestWithRole = UserCreateRequest("admin", "admin@example.com", "pass", Some(Role.Admin))
    assertEquals(requestWithRole.role, Some(Role.Admin))
  }
}
