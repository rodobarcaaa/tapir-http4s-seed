package com.example.shared.infrastructure.slick

import com.example.shared.domain.page.{PageRequest, PageResponse}

import scala.concurrent.ExecutionContext.Implicits.global

trait DynamicSortPage {
  self: HasSlickPgProvider =>

  import profile.api._
  import slick.ast.Ordering
  import slick.ast.Ordering.Direction
  import slick.lifted.{ColumnOrdered, Ordered}

  type ColumnOrdering = (String, Direction)

  trait SortColumnSelector {
    val select: Map[String, Rep[_]]

    def columnRep(field: String): Rep[_] = select.getOrElse(
      field,
      throw new IllegalArgumentException(s"Sort field($field) not allowed")
    )
  }

  def extractSorts(sort: Option[String]): Seq[ColumnOrdering] = sort.fold(Seq.empty[ColumnOrdering]) { sortBy =>
    val signedFieldPattern = """([+-]?)(\w+)""".r
    signedFieldPattern.findAllIn(sortBy).toSeq.map {
      case signedFieldPattern("-", field) => (field, Ordering.Desc)
      case signedFieldPattern(_, field)   => (field, Ordering.Asc)
    }
  }

  implicit class SortableQuery[A <: Table[B] with SortColumnSelector, B](query: Query[A, B, Seq]) {

    def dynamicSortBy(sorts: Seq[ColumnOrdering]): Query[A, B, Seq] = {
      sorts.foldRight(query) { // Fold right is reversing order
        case ((sortColumn, sortOrder), queryToSort) =>
          val sortColumnRep: A => Rep[_]      = _.columnRep(sortColumn)
          val sortOrderRep: Rep[_] => Ordered = ColumnOrdered(_, Ordering(sortOrder))
          queryToSort.sortBy(sortColumnRep)(sortOrderRep)
      }
    }

    def sortSlick(sort: Option[String], sortDefault: Option[String]): Query[A, B, Seq] = {
      val sorts = extractSorts(sort.orElse(sortDefault))
      if (sorts.isEmpty) query else query.dynamicSortBy(sorts)
    }

    def withSortPage(
        pr: PageRequest,
        sortDefault: Option[String] = Some("-id")
    ): DBIOAction[PageResponse[B], NoStream, ER] = {
      for {
        total    <- query.length.result
        elements <- sortSlick(pr.sort, sortDefault).pageSlick(pr).result
      } yield PageResponse(pr, total, elements)
    }
  }

}
