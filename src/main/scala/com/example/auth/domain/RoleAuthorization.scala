package com.example.auth.domain

import cats.data.{Validated, ValidatedNel}
import com.example.shared.infrastructure.http.Fail

object RoleAuthorization {

  // Primary validation methods using Cats Validated
  def validateAdmin(userInfo: UserInfo): ValidatedNel[String, Unit] = {
    Validated.condNel(userInfo.role == Role.Admin, (), "Admin role required")
  }

  def validateCustomer(userInfo: UserInfo): ValidatedNel[String, Unit] = {
    Validated.condNel(userInfo.role == Role.Customer, (), "Customer role required")
  }

  def validateAnyRole(userInfo: UserInfo, allowedRoles: Set[Role]): ValidatedNel[String, Unit] = {
    Validated.condNel(
      allowedRoles.contains(userInfo.role),
      (),
      s"One of the following roles required: ${allowedRoles.map(Role.toString).mkString(", ")}"
    )
  }

  // Helper to convert validation to Either for HTTP endpoints
  private def toEither(validation: ValidatedNel[String, Unit]): Either[Fail, Unit] = {
    validation.toEither.left.map(_ => Fail.Forbidden)
  }

  // Convenience methods that return Either for backward compatibility
  def requireAdmin(userInfo: UserInfo): Either[Fail, Unit] = {
    toEither(validateAdmin(userInfo))
  }

  def requireCustomer(userInfo: UserInfo): Either[Fail, Unit] = {
    toEither(validateCustomer(userInfo))
  }

  def requireAnyRole(userInfo: UserInfo, allowedRoles: Set[Role]): Either[Fail, Unit] = {
    toEither(validateAnyRole(userInfo, allowedRoles))
  }

  def isAdmin(userInfo: UserInfo): Boolean    = userInfo.role == Role.Admin
  def isCustomer(userInfo: UserInfo): Boolean = userInfo.role == Role.Customer
}
