package com.example.books.infrastructure.helpers

import com.example.books.domain.publisher.PublisherMother
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite

trait PublisherHelper {
  self: HasHttp4sRoutesSuite =>

  private lazy val publisherService = module.publisherService

  def createRandomPublisher: Id = {
    val publisher = PublisherMother.random
    publisherService.create(publisher).unsafeRunSync()
    publisher.id
  }

  def deletePublisher(id: Id): Unit = publisherService.delete(id).unsafeRunSync()

}
