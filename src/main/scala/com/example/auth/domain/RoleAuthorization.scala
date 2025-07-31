package com.example.auth.domain

import com.example.shared.infrastructure.http.Fail

object RoleAuthorization {
  def requireAdmin(userInfo: UserInfo): Either[Fail, Unit] = {
    if (userInfo.role == Role.Admin) Right(())
    else Left(Fail.Forbidden)
  }

  def requireCustomer(userInfo: UserInfo): Either[Fail, Unit] = {
    if (userInfo.role == Role.Customer) Right(())
    else Left(Fail.Forbidden)
  }

  def requireAnyRole(userInfo: UserInfo, allowedRoles: Set[Role]): Either[Fail, Unit] = {
    if (allowedRoles.contains(userInfo.role)) Right(())
    else Left(Fail.Forbidden)
  }

  def isAdmin(userInfo: UserInfo): Boolean = userInfo.role == Role.Admin
  def isCustomer(userInfo: UserInfo): Boolean = userInfo.role == Role.Customer
}