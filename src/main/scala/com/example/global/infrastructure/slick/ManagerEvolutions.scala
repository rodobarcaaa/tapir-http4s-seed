package com.example.global.infrastructure.slick

import com.example.books.infrastructure.slick.BookMapping
import com.example.shared.infrastructure.slick.HasSlickPgProvider

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

object ManagerEvolutions extends HasSlickPgProvider with BookMapping {

  import profile.api._

  private val allSchemas = {
    Authors.schema ++
      Publishers.schema ++
      Books.schema
  }

  private val evolutionsFolder = "src/main/resources/"
  private val evolutionsFile   = evolutionsFolder + "migrations.sql"

  def initialize(): Unit = {
    val folder = new File(evolutionsFolder)

    if (!folder.exists) folder.mkdirs()

    if (!Files.exists(Paths.get(evolutionsFile))) {
      val writer = new PrintWriter(evolutionsFile)

      writer.write("# --- !Ups\n\n")
      allSchemas.createStatements.foreach(s => writer.write(s + ";\n"))

      writer.write("\n\n# --- !Downs\n\n")
      allSchemas.dropStatements.foreach(s => writer.write(s + ";\n"))

      writer.close()
    }
  }

}
