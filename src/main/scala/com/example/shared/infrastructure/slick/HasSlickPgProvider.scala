package com.example.shared.infrastructure.slick

import cats.effect.IO
import com.example.MainModule
import com.example.shared.domain.page.{PageRequest, PageResponse}
import com.example.shared.infrastructure.slick.HasSlickPgProvider._
import slick.jdbc.{ResultSetConcurrency, ResultSetType}

import scala.concurrent.ExecutionContext.Implicits.global

trait HasSlickPgProvider extends MainModule {

  val profile: PgProfile = PgProfile

  import profile.api._

  val db: Database = Database.forURL(
    url = dbConfig.url,
    user = dbConfig.user,
    password = dbConfig.password.value,
    driver = dbConfig.driver
  )

  type EW  = Effect.Write
  type ER  = Effect.Read
  type EWR = EW with ER

  val defaultFilter = LiteralColumn(true)

  implicit class SlickDBOps(val db: Database) {
    def runIO[R](dbio: DBIO[R]): IO[R] = IO.fromFuture(IO(db.run(dbio)))

    def runIOUnit[R](dbio: DBIO[R]): IO[Unit] = runIO(dbio).map(_ => ())

    def runWithCheckRowsAffectedEW[R](
        dbio: DBIOAction[Int, NoStream, EW],
        exception: Throwable = new IllegalStateException(NotRowsAffectedMSG)
    ): IO[Unit] = runIO(dbio).flatMap(rowsAffected => IO.raiseUnless(rowsAffected > 0)(exception))

    def runWithCheckRowsAffectedEWR[R](
        dbio: DBIOAction[Int, NoStream, EWR],
        exception: Throwable = new IllegalStateException(NotRowsAffectedMSG)
    ): IO[Unit] = runIO(dbio).flatMap(rowsAffected => IO.raiseUnless(rowsAffected > 0)(exception))

    def runWithCheckRowsAffectedT[R](
        dbio: DBIOAction[Int, NoStream, EW with Effect.Transactional],
        exception: Throwable = new IllegalStateException(NotRowsAffectedMSG)
    ): IO[Unit] = runIO(dbio).flatMap(rowsAffected => IO.raiseUnless(rowsAffected > 0)(exception))

    def runWithCheckNumRowsAffected[R](
        dbio: DBIOAction[Int, NoStream, EW],
        num: Int,
        exception: Throwable = new IllegalStateException(IncorrectRowsAffectedMSG)
    ): IO[Unit] = runIO(dbio).flatMap(rowsAffected => IO.raiseUnless(rowsAffected == num)(exception))

  }

  implicit class PageQuery[A <: Any, B](query: Query[A, B, Seq]) {

    def pageSlick(pr: PageRequest): Query[A, B, Seq] =
      query.drop(pr.offset).take(pr.size)

    def withPage(pr: PageRequest): DBIOAction[PageResponse[B], NoStream, ER] = for {
      total    <- query.length.result
      elements <- query.pageSlick(pr).result
    } yield PageResponse(pr, total, elements)

  }

  implicit class StreamExtensions(query: Query[?, ?, Seq]) {
    def resultStream[T](fetchSize: Int = 10000): DBIOAction[Any, Streaming[T], Nothing] =
      query.result
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = fetchSize
        )
        .transactionally
        .asInstanceOf[DBIOAction[Any, Streaming[T], Nothing]]
  }

}

object HasSlickPgProvider {
  val NotRowsAffectedMSG       = "Not Rows Affected"
  val IncorrectRowsAffectedMSG = "Incorrect Rows Affected"
}
