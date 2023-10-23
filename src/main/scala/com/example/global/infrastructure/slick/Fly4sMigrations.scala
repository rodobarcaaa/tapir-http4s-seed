package com.example.global.infrastructure.slick

import cats.effect._
import cats.implicits._
import com.example.books.infrastructure.slick.BookMapping
import com.example.global.infrastructure.http.DBConfig
import com.example.shared.infrastructure.slick.HasSlickPgProvider
import fly4s.core.Fly4s
import fly4s.core.data._

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

object Fly4sMigrations extends HasSlickPgProvider with BookMapping {

  private val migrationsFolder = "src/main/resources/db/migration"

  def initialize(): Unit = {
    val folder = new File(migrationsFolder)
    if (!folder.exists) folder.mkdirs()

    val migrationFile = migrationsFolder + "/V003__create_books_table.sql" // change on demand

    import profile.api._
    val schemas = Books.schema

    if (!Files.exists(Paths.get(migrationFile))) {
      val writer = new PrintWriter(migrationFile)
      schemas.createStatements.foreach(s => writer.write(s + ";\n"))
      writer.close()
    }
  }

  def migrateDb(dbConfig: DBConfig): Resource[IO, MigrateResult] = {
    initialize()

    Fly4s
      .make[IO](
        url = dbConfig.url,
        user = dbConfig.user.some,
        password = dbConfig.password.toCharArray.some,
        config = Fly4sConfig(baselineOnMigrate = true)
      )
      .evalMap(_.migrate)
  }

}
