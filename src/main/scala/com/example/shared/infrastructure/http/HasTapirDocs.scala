package com.example.shared.infrastructure.http

trait HasTapirDocs extends HasTapirEndpoints {
  lazy val docs: ServerDocs = endpoints.map(_.endpoint)
}
