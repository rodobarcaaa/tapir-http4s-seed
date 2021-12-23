package com.example.shared.infrastructure.http

import com.example.MainModule

trait HasHttp4sRoutesSuite extends munit.Http4sHttpRoutesSuite {
  lazy val module: MainModule = MainModule.initialize
}
