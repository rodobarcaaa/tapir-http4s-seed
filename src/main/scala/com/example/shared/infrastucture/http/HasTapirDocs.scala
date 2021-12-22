package com.example.shared.infrastucture.http

trait HasTapirDocs extends HasTapirEndpoints {
  lazy val docs: ServerDocs = endpoints.map(_.endpoint)
}
