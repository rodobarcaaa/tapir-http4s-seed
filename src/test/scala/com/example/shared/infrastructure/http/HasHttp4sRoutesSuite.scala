package com.example.shared.infrastructure.http

import cats.effect.IO
import com.example.MainModule
import com.example.global.infrastructure.slick.Fly4sMigrations

trait HasHttp4sRoutesSuite extends munit.Http4sHttpRoutesSuite {
  lazy val module: MainModule = MainModule.initialize

  Fly4sMigrations.migrateDb(module.dbConfig).use(_ => IO.unit).unsafeRunSync()
}
