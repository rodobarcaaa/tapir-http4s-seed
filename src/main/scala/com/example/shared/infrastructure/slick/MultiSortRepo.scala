package com.example.shared.infrastructure.slick

import com.example.shared.domain.page.{PageRequest, PageResponse}

import scala.concurrent.ExecutionContext.Implicits.global

trait MultiSortRepo extends DynamicSortPage {
  self: HasSlickPgProvider =>

  import profile.api._
  import slick.ast.Ordering
  import slick.lifted.{ColumnOrdered, Ordered}

  type MultiTables

  implicit def select(multiTables: MultiTables, field: String): Rep[?]

  implicit class MultiSorteableQuery[A <: Any, B](query: Query[A, B, Seq]) {

    def dynamicJoinSortBy(sorts: Seq[ColumnOrdering])(implicit select: (A, String) => Rep[?]): Query[A, B, Seq] = {
      sorts.foldRight(query) { // Fold right is reversing order
        case ((sortColumn, sortOrder), queryToSort) =>
          val sortColumnRep: A => Rep[?]      = select(_, sortColumn)
          val sortOrderRep: Rep[?] => Ordered = ColumnOrdered(_, Ordering(sortOrder))
          queryToSort.sortBy(sortColumnRep)(sortOrderRep)
      }
    }

    def sortSlick(sort: Option[String], sortDefault: Option[String])(implicit
        select: (A, String) => Rep[?]
    ): Query[A, B, Seq] = {
      val sorts = extractSorts(sort.orElse(sortDefault))
      if (sorts.isEmpty) query else query.dynamicJoinSortBy(sorts)
    }

    def withSortPage(pr: PageRequest, sortDefault: Option[String] = Some("-id"))(implicit
        select: (A, String) => Rep[?]
    ): DBIOAction[PageResponse[B], NoStream, ER] = for {
      total    <- query.length.result
      elements <- sortSlick(pr.sort, sortDefault).pageSlick(pr).result
    } yield PageResponse(pr, total, elements)

  }
}
