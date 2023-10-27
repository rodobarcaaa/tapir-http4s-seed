package com.example.shared.infrastructure.config

final case class Sensitive(value: String) extends AnyVal {
  override def toString: String = "***"
}
