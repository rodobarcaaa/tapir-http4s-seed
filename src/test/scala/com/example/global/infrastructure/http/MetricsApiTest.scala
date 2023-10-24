package com.example.global.infrastructure.http

import cats.effect.IO
import com.example.books.infrastructure.codecs.BookCodecs
import com.example.shared.infrastructure.http.HasHttp4sRoutesSuite
import org.http4s._

class MetricsApiTest extends HasHttp4sRoutesSuite with BookCodecs {

  override val routes: HttpRoutes[IO] = module.metricsApi.routes

  test(GET(uri"metrics")) { response =>
    assertEquals(response.status, Status.Ok)
  }

}
