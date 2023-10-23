package com.example.books.infrastructure.http

import sttp.tapir.{EndpointInput, query}

trait HasQueryFilter {
  val filter: EndpointInput.Query[Option[String]] = query[Option[String]]("filter")
}
