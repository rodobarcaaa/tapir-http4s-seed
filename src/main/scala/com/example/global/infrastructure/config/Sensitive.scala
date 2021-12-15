package com.example.global.infrastructure.config

case class Sensitive(value: String) extends AnyVal {
  override def toString: String = "***"
}
