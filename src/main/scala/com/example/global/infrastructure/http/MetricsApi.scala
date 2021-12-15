package com.example.global.infrastructure.http

import cats.data.NonEmptyList
import cats.effect.{IO, Resource}
import com.example.shared.infrastucture.http.ServerEndpoints
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import org.http4s.metrics.MetricsOps
import org.http4s.metrics.prometheus.Prometheus
import sttp.tapir._

import java.io.StringWriter

class MetricsApi {

  private val registry: CollectorRegistry = CollectorRegistry.defaultRegistry

  private val service = "Metrics"

  private val metrics = endpoint
    .tag(service)
    .in(service.toLowerCase)
    .get
    .out(stringBody)
    .serverLogicSuccess { _ =>
      IO {
        val writer = new StringWriter
        TextFormat.write004(writer, registry.metricFamilySamples)
        writer.toString
      }
    }

  val endpoints: ServerEndpoints = NonEmptyList.of(metrics)

  val prometheusOps: Resource[IO, MetricsOps[IO]] = Prometheus.metricsOps[IO](registry)
}
