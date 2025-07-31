package com.example.auth.domain

enum Role:
  case Admin, Customer

object Role {
  def fromString(s: String): Option[Role] = s.toLowerCase match {
    case "admin"    => Some(Admin)
    case "customer" => Some(Customer)
    case _          => None
  }

  def toString(role: Role): String = role match {
    case Admin    => "admin"
    case Customer => "customer"
  }
}