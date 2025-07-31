package com.example.books.infrastructure.http

import cats.effect.IO
import com.example.auth.domain.{Role, UserCreateRequest}
import com.example.books.domain.book.BookMother
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.books.infrastructure.helpers._
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import io.circe.generic.auto._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._

class RoleBasedAuthorizationTest extends HasHttp4sRoutesSuite with BookCodecs with AuthorHelper with PublisherHelper {

  private lazy val authService = module.authService

  // Create admin and customer test users
  private def createAdminToken(): String = {
    val createRequest = UserCreateRequest(
      username = s"admin-${System.currentTimeMillis()}",
      email = s"admin-${System.currentTimeMillis()}@example.com",
      password = "password123",
      role = Some(Role.Admin)
    )
    authService.register(createRequest).unsafeRunSync().token
  }

  private def createCustomerToken(): String = {
    val createRequest = UserCreateRequest(
      username = s"customer-${System.currentTimeMillis()}",
      email = s"customer-${System.currentTimeMillis()}@example.com",
      password = "password123",
      role = Some(Role.Customer)
    )
    authService.register(createRequest).unsafeRunSync().token
  }

  private def jwtAuthHeader(token: String): Header.Raw = {
    import org.http4s.headers.Authorization
    Authorization(Credentials.Token(AuthScheme.Bearer, token)).toRaw1
  }

  // Test data setup
  val authorId    = createRandomAuthor
  val publisherId = createRandomPublisher
  val book        = BookMother.random(authorId, publisherId)

  // Test Book API endpoints
  override val routes: HttpRoutes[IO] = module.bookApi.routes

  test(POST(book, uri"books").withHeaders(jwtAuthHeader(createAdminToken()))).alias("Admin CREATE") { response =>
    assertEquals(response.status, Status.Created)
  }

  test(POST(book, uri"books").withHeaders(jwtAuthHeader(createCustomerToken()))).alias("Customer CREATE - FORBIDDEN") {
    response =>
      assertEquals(response.status, Status.Forbidden)
  }

  test(PUT(book, uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createAdminToken())))
    .alias("Admin UPDATE") { response =>
      assertEquals(response.status, Status.NoContent)
    }

  test(PUT(book, uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createCustomerToken())))
    .alias("Customer UPDATE - FORBIDDEN") { response =>
      assertEquals(response.status, Status.Forbidden)
    }

  test(DELETE(uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createAdminToken()))).alias("Admin DELETE") {
    response =>
      assertEquals(response.status, Status.NoContent)
  }

  test(DELETE(uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createCustomerToken())))
    .alias("Customer DELETE - FORBIDDEN") { response =>
      assertEquals(response.status, Status.Forbidden)
    }

  test(GET(uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createAdminToken()))).alias("Admin READ") {
    response =>
      assertEquals(response.status, Status.Ok)
  }

  test(GET(uri"books" / s"${book.id.value}").withHeaders(jwtAuthHeader(createCustomerToken()))).alias("Customer READ") {
    response =>
      assertEquals(response.status, Status.Ok)
  }

  test(GET(uri"books").withHeaders(jwtAuthHeader(createAdminToken()))).alias("Admin LIST") { response =>
    assertEquals(response.status, Status.Ok)
  }

  test(GET(uri"books").withHeaders(jwtAuthHeader(createCustomerToken()))).alias("Customer LIST") { response =>
    assertEquals(response.status, Status.Ok)
  }

  test(GET(uri"books" / s"${book.id.value}")).alias("Unauthenticated READ - UNAUTHORIZED") { response =>
    assertEquals(response.status, Status.Unauthorized)
  }

  test(GET(uri"books")).alias("Unauthenticated LIST - UNAUTHORIZED") { response =>
    assertEquals(response.status, Status.Unauthorized)
  }
}
