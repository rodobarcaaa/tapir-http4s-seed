package com.example.shared.infrastucture.http

case class HttpConfig(host: String, port: Int) {
  def toUrl(base: String): String = s"$base$host:$port"
}
