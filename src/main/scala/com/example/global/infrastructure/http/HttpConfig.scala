package com.example.global.infrastructure.http

case class HttpConfig(host: String, port: Int) {
  def toUrl(base: String): String = s"$base$host:$port"
}
