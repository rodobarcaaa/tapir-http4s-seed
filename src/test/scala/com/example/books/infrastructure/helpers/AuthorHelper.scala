package com.example.books.infrastructure.helpers

import com.example.books.domain.author.AuthorMother
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite

trait AuthorHelper {
  self: HasHttp4sRoutesSuite =>

  private lazy val authorService = module.authorService

  def createRandomAuthor: Id = {
    val author = AuthorMother.random
    authorService.create(author).unsafeRunSync()
    author.id
  }

  def deleteAuthor(id: Id): Unit = authorService.delete(id).unsafeRunSync()

}
