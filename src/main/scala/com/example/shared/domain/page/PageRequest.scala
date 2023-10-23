package com.example.shared.domain.page

final case class PageRequest(page: Int, size: Int, sort: Option[String] = None) {
  require(page > 0, "page must be greater than 0")
  require(size <= PageRequest.MAX_PAGE_SIZE, s"size must be less or equal to ${PageRequest.MAX_PAGE_SIZE}")

  val offset: Int = (page - 1) * size
}

object PageRequest {
  val MAX_PAGE_SIZE = 100
}
