package com.example.global.infrastructure.slick

import cats.effect._
import cats.implicits._
import com.example.books.infrastructure.slick.BookMapping
import com.example.shared.infrastructure.slick.HasSlickPgProvider
import fly4s.core.Fly4s
import fly4s.core.data._

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

object Fly4sModule extends HasSlickPgProvider with BookMapping {

  private val migrationsFolder = "src/main/resources/db/migration"

  import profile.api._
  private val schemas = Books.schema // add more schemas on demand using ++ operator

  private val migrationFile = migrationsFolder + "/V003__create_books_table.sql" // change name on demand

  private val witterAssistant = Resource.make {
    for {
      folder <- IO(new File(migrationsFolder))
      _      <- IO.unlessA(folder.exists())(IO(folder.mkdirs()).void)

      _ <- IO.unlessA(Files.exists(Paths.get(migrationFile))) {
             IO {
               val w = new PrintWriter(migrationFile)
               w.write(schemas.createStatements.mkString("\n"))
               w.close()
             }
           }
    } yield ()
  }(_ => IO.unit)

  private lazy val fly4sConfig = Fly4s.make[IO](
    url = dbConfig.url,
    user = dbConfig.user.some,
    password = dbConfig.password.value.toCharArray.some,
    config = Fly4sConfig(baselineOnMigrate = true)
  )

  def migrateDbResource: Resource[IO, MigrateResult] = witterAssistant *> fly4sConfig.evalMap(_.migrate)

}
