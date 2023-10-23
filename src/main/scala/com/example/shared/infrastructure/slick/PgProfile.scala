package com.example.shared.infrastructure.slick

import com.github.tminglei.slickpg._
import slick.ast.Library._
import slick.ast._
import slick.lifted.FunctionSymbolExtensionMethods._

trait PgProfile
    extends ExPostgresProfile
    with PgArraySupport
    with PgDate2Support
    with PgRangeSupport
    with PgHStoreSupport
    with PgSearchSupport
    with PgNetSupport
    with PgLTreeSupport {

  override val api: MyAPI.type = MyAPI

  object MyAPI
      extends API
      with ArrayImplicits
      with DateTimeImplicits
      with NetImplicits
      with LTreeImplicits
      with RangeImplicits
      with HStoreImplicits
      with SearchImplicits
      with SearchAssistants {

    implicit val strListTypeMapper: DriverJdbcType[List[String]] =
      new SimpleArrayJdbcType[String]("text").to(_.toList)

    implicit val strSeqTypeMapper: DriverJdbcType[Seq[String]] = new SimpleArrayJdbcType[String]("text").to(_.toSeq)

    val ArrayAgg = new SqlAggregateFunction("array_agg")

    // Implement the aggregate function as an extension method:
    implicit class ArrayAggColumnQueryExtensionMethods[P, C[_]](val q: Query[Rep[P], _, C]) {
      def arrayAgg[B](implicit tm: TypedType[List[B]]) =
        ArrayAgg.column[List[B]](q.toNode)
    }
  }
}

object PgProfile extends PgProfile
