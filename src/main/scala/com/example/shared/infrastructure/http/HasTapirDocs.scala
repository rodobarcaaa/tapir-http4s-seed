package com.example.shared.infrastructure.http

trait HasTapirDocs extends HasTapirEndpoints {
  // docs to expose from endpoints
  lazy val docs: ServerDocs = endpoints.map(_.endpoint)
}
