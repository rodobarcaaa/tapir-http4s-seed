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
  val authorId = createRandomAuthor
  val publisherId = createRandomPublisher
  val book = BookMother.random(authorId, publisherId)

  // Test Book API endpoints
  override val routes: HttpRoutes[IO] = module.bookApi.routes

  test("Admin should be able to create books") {
    val adminToken = createAdminToken()
    val response = POST(book, uri"books").withHeaders(jwtAuthHeader(adminToken)).unsafeRunSync()
    assertEquals(response.status, Status.Created)
  }

  test("Customer should NOT be able to create books") {
    val customerToken = createCustomerToken()
    val response = POST(book, uri"books").withHeaders(jwtAuthHeader(customerToken)).unsafeRunSync()
    assertEquals(response.status, Status.Forbidden)
  }

  test("Admin should be able to update books") {
    val adminToken = createAdminToken()
    val bookId = book.id.value
    val response = PUT(book, uri"books" / s"$bookId").withHeaders(jwtAuthHeader(adminToken)).unsafeRunSync()
    assertEquals(response.status, Status.NoContent)
  }

  test("Customer should NOT be able to update books") {
    val customerToken = createCustomerToken()
    val bookId = book.id.value
    val response = PUT(book, uri"books" / s"$bookId").withHeaders(jwtAuthHeader(customerToken)).unsafeRunSync()
    assertEquals(response.status, Status.Forbidden)
  }

  test("Admin should be able to delete books") {
    val adminToken = createAdminToken()
    val bookId = book.id.value
    val response = DELETE(uri"books" / s"$bookId").withHeaders(jwtAuthHeader(adminToken)).unsafeRunSync()
    assertEquals(response.status, Status.NoContent)
  }

  test("Customer should NOT be able to delete books") {
    val customerToken = createCustomerToken()
    val bookId = book.id.value
    val response = DELETE(uri"books" / s"$bookId").withHeaders(jwtAuthHeader(customerToken)).unsafeRunSync()
    assertEquals(response.status, Status.Forbidden)
  }

  test("Admin should be able to read books") {
    val adminToken = createAdminToken()
    val bookId = book.id.value
    val response = GET(uri"books" / s"$bookId").withHeaders(jwtAuthHeader(adminToken)).unsafeRunSync()
    assertEquals(response.status, Status.Ok)
  }

  test("Customer should be able to read books") {
    val customerToken = createCustomerToken()
    val bookId = book.id.value
    val response = GET(uri"books" / s"$bookId").withHeaders(jwtAuthHeader(customerToken)).unsafeRunSync()
    assertEquals(response.status, Status.Ok)
  }

  test("Admin should be able to list books") {
    val adminToken = createAdminToken()
    val response = GET(uri"books").withHeaders(jwtAuthHeader(adminToken)).unsafeRunSync()
    assertEquals(response.status, Status.Ok)
  }

  test("Customer should be able to list books") {
    val customerToken = createCustomerToken()
    val response = GET(uri"books").withHeaders(jwtAuthHeader(customerToken)).unsafeRunSync()
    assertEquals(response.status, Status.Ok)
  }

  test("Unauthenticated users should NOT be able to read books") {
    val bookId = book.id.value
    val response = GET(uri"books" / s"$bookId").unsafeRunSync()
    assertEquals(response.status, Status.Unauthorized)
  }

  test("Unauthenticated users should NOT be able to list books") {
    val response = GET(uri"books").unsafeRunSync()
    assertEquals(response.status, Status.Unauthorized)
  }
}