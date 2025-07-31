package com.example.auth.domain

import cats.data.Validated
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.Fail
import munit.FunSuite

import java.time.Instant
import java.util.UUID

class RoleAuthorizationTest extends FunSuite {

  private val adminUser = UserInfo(
    id = Id(UUID.randomUUID()),
    username = "admin",
    email = "admin@example.com",
    role = Role.Admin,
    createdAt = Instant.now()
  )

  private val customerUser = UserInfo(
    id = Id(UUID.randomUUID()),
    username = "customer",
    email = "customer@example.com",
    role = Role.Customer,
    createdAt = Instant.now()
  )

  // Test Either-based methods (for backward compatibility)
  test("requireAdmin should allow admin users") {
    val result = RoleAuthorization.requireAdmin(adminUser)
    assertEquals(result, Right(()))
  }

  test("requireAdmin should reject customer users") {
    val result = RoleAuthorization.requireAdmin(customerUser)
    assert(result.isLeft)
    assertEquals(result, Left(Fail.Forbidden))
  }

  test("requireCustomer should allow customer users") {
    val result = RoleAuthorization.requireCustomer(customerUser)
    assertEquals(result, Right(()))
  }

  test("requireCustomer should reject admin users") {
    val result = RoleAuthorization.requireCustomer(adminUser)
    assert(result.isLeft)
    assertEquals(result, Left(Fail.Forbidden))
  }

  test("requireAnyRole should allow users with matching roles") {
    val result1 = RoleAuthorization.requireAnyRole(adminUser, Set(Role.Admin, Role.Customer))
    assertEquals(result1, Right(()))

    val result2 = RoleAuthorization.requireAnyRole(customerUser, Set(Role.Customer))
    assertEquals(result2, Right(()))
  }

  test("requireAnyRole should reject users without matching roles") {
    val result = RoleAuthorization.requireAnyRole(customerUser, Set(Role.Admin))
    assert(result.isLeft)
    assertEquals(result, Left(Fail.Forbidden))
  }

  // Test Cats Validated methods (new validation approach)
  test("validateAdmin should allow admin users") {
    val result = RoleAuthorization.validateAdmin(adminUser)
    assertEquals(result, Validated.valid(()))
  }

  test("validateAdmin should reject customer users with meaningful error") {
    val result = RoleAuthorization.validateAdmin(customerUser)
    assertEquals(result, Validated.invalidNel("Admin role required"))
  }

  test("validateCustomer should allow customer users") {
    val result = RoleAuthorization.validateCustomer(customerUser)
    assertEquals(result, Validated.valid(()))
  }

  test("validateCustomer should reject admin users with meaningful error") {
    val result = RoleAuthorization.validateCustomer(adminUser)
    assertEquals(result, Validated.invalidNel("Customer role required"))
  }

  test("validateAnyRole should allow users with matching roles") {
    val result1 = RoleAuthorization.validateAnyRole(adminUser, Set(Role.Admin, Role.Customer))
    assertEquals(result1, Validated.valid(()))

    val result2 = RoleAuthorization.validateAnyRole(customerUser, Set(Role.Customer))
    assertEquals(result2, Validated.valid(()))
  }

  test("validateAnyRole should reject users without matching roles with descriptive error") {
    val result = RoleAuthorization.validateAnyRole(customerUser, Set(Role.Admin))
    assertEquals(result, Validated.invalidNel("One of the following roles required: admin"))
  }

  test("isAdmin should correctly identify admin users") {
    assert(RoleAuthorization.isAdmin(adminUser))
    assert(!RoleAuthorization.isAdmin(customerUser))
  }

  test("isCustomer should correctly identify customer users") {
    assert(RoleAuthorization.isCustomer(customerUser))
    assert(!RoleAuthorization.isCustomer(adminUser))
  }
}
