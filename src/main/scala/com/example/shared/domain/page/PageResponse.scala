package com.example.shared.domain.page

final case class PageResponse[A](
    page: Int,
    size: Int,
    total: Int,
    sort: Option[String] = None,
    elements: Seq[A]
) {
  def map[B](f: A => B): PageResponse[B] = copy(elements = elements.map(f))
}

object PageResponse {

  def apply[A](sp: PageRequest, total: Int, elements: Seq[A]): PageResponse[A] = PageResponse(
    sp.page,
    sp.size,
    total,
    sp.sort,
    elements
  )

}
