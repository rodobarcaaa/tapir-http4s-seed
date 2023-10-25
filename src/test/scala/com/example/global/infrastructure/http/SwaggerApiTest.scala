package com.example.global.infrastructure.http

import cats.effect.IO
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import org.http4s._

class SwaggerApiTest extends HasHttp4sRoutesSuite with BookCodecs {

  override val routes: HttpRoutes[IO] = module.httpApi.swaggerRoutes

  test(GET(uri"docs")) { response =>
    assertEquals(response.status, Status.PermanentRedirect)
  }

  test(GET(uri"docs/index.html?url=/docs/docs.yaml")) { response =>
    assertEquals(response.status, Status.Ok)
  }

}
