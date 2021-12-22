package com.example.global.infrastructure.http

import cats.effect.{IO, Resource}
import cats.implicits._
import com.example.books.infrastructure.http.BookApi
import com.example.global.infrastructure.http.HttpApi._
import com.example.shared.infrastucture.http.{HttpConfig, ServerRoutes}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.Metrics
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class HttpApi(
    metricsApi: MetricsApi,
    bookApi: BookApi,
    config: HttpConfig
) {

  private lazy val apiDocs   = metricsApi.docs <+> bookApi.docs
  private lazy val apiRoutes = bookApi.routes

  private lazy val swaggerRoutes: ServerRoutes = Http4sServerInterpreter[IO]().toRoutes {
    SwaggerInterpreter().fromEndpoints[IO](apiDocs, "Books Store", version)
  }

  lazy val resource: Resource[IO, Server] = for {
    prometheusRoutes <- metricsApi.prometheusOps.map(m => Metrics[IO](m)(apiRoutes))
    app               = Router("/" -> (prometheusRoutes <+> swaggerRoutes <+> apiRoutes)).orNotFound
    resource         <- BlazeServerBuilder[IO]
                          .bindHttp(config.port, config.host)
                          .withHttpApp(app)
                          .resource
  } yield resource
}

object HttpApi {
  val version: String = Option(classOf[HttpApi].getPackage.getImplementationVersion).getOrElse("DEV")
}
