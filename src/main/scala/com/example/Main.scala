package com.example

import cats.effect.{IO, Resource, ResourceApp}
import com.example.global.infrastructure.slick.Fly4sModule

object Main extends ResourceApp.Forever {

  /** Creating a resource that will be used to run the application composed of three steps:
    *
    *   - the first creates the main module (which loads the configuration and initializes the prometheus metrics)
    *   - the second migrates the database if needed (it will create the new migration file if it don't exist)
    *   - the third allocates the http api resource (which starts the http server and exposes the endpoints)
    *
    * ResourceApp.Forever uses the last resource through a process without termination (so the http server will be
    * available while our application is running).
    */
  override def run(args: List[String]): Resource[IO, Unit] = for {
    module <- MainModule.resource
    _      <- Fly4sModule.migrateDbResource
    _      <- module.httpApi.resource
  } yield ()

}
