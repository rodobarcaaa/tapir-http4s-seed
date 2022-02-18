package com.example.shared.infrastructure.config

case class Sensitive(value: String) extends AnyVal {
  override def toString: String = "***"
}
