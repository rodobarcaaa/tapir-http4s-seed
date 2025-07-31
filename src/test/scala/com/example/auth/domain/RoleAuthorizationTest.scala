package com.example.auth.domain

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

  test("isAdmin should correctly identify admin users") {
    assert(RoleAuthorization.isAdmin(adminUser))
    assert(!RoleAuthorization.isAdmin(customerUser))
  }

  test("isCustomer should correctly identify customer users") {
    assert(RoleAuthorization.isCustomer(customerUser))
    assert(!RoleAuthorization.isCustomer(adminUser))
  }
}