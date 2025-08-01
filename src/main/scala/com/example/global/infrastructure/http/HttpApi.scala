package com.example.global.infrastructure.http

import cats.effect.{IO, Resource}
import cats.implicits._
import com.example.auth.infrastructure.http.AuthApi
import com.example.books.infrastructure.http._
import com.example.shared.infrastructure.http.ServerRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.{Logger, Metrics}
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.swagger.bundle.SwaggerInterpreter

class HttpApi(
    metricsApi: MetricsApi,
    authorApi: AuthorApi,
    publisherApi: PublisherApi,
    bookApi: BookApi,
    authApi: AuthApi,
    config: HttpConfig
) {

  private lazy val apiDocs = publisherApi.docs <+> authorApi.docs <+> bookApi.docs <+> authApi.docs

  private lazy val apiRoutes =
    metricsApi.routes <+> publisherApi.routes <+> authorApi.routes <+> bookApi.routes <+> authApi.routes

  lazy val swaggerRoutes: ServerRoutes = Http4sServerInterpreter[IO]().toRoutes {
    SwaggerInterpreter().fromEndpoints[IO](apiDocs, "Books Store", HttpApi.version)
  }

  lazy val resource: Resource[IO, Server] = for {
    prometheusRoutes <- metricsApi.prometheusOps.map(m => Metrics[IO](m)(apiRoutes))
    httpApp           = Router("/" -> (prometheusRoutes <+> swaggerRoutes <+> apiRoutes)).orNotFound
    finalApp          = Logger.httpApp(logHeaders = true, logBody = true)(httpApp)
    resource         <- BlazeServerBuilder[IO]
                          .bindHttp(config.port, config.host)
                          .withHttpApp(finalApp)
                          .resource
  } yield resource
}

object HttpApi {
  val version: String = Option(classOf[HttpApi].getPackage.getImplementationVersion).getOrElse("DEV")
}
