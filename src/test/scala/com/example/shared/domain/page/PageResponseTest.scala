package com.example.shared.domain.page

import munit.FunSuite

class PageResponseTest extends FunSuite {

  test("PageResponse should create with all fields") {
    val response = PageResponse(1, 10, 100, Some("name"), Seq("item1", "item2"))
    assertEquals(response.page, 1)
    assertEquals(response.size, 10)
    assertEquals(response.total, 100)
    assertEquals(response.sort, Some("name"))
    assertEquals(response.elements, Seq("item1", "item2"))
  }

  test("PageResponse should create without sort") {
    val response = PageResponse(1, 10, 50, None, Seq("item1"))
    assertEquals(response.page, 1)
    assertEquals(response.size, 10)
    assertEquals(response.total, 50)
    assertEquals(response.sort, None)
    assertEquals(response.elements, Seq("item1"))
  }

  test("PageResponse map should transform elements") {
    val response = PageResponse(1, 10, 2, None, Seq(1, 2))
    val mapped = response.map(_.toString)
    assertEquals(mapped.page, 1)
    assertEquals(mapped.size, 10)
    assertEquals(mapped.total, 2)
    assertEquals(mapped.sort, None)
    assertEquals(mapped.elements, Seq("1", "2"))
  }

  test("PageResponse companion apply should create from PageRequest") {
    val pageRequest = PageRequest(2, 15, Some("name"))
    val elements = Seq("a", "b", "c")
    val response = PageResponse(pageRequest, 45, elements)
    
    assertEquals(response.page, 2)
    assertEquals(response.size, 15)
    assertEquals(response.total, 45)
    assertEquals(response.sort, Some("name"))
    assertEquals(response.elements, elements)
  }

  test("PageResponse companion apply should handle None sort") {
    val pageRequest = PageRequest(1, 10)
    val elements = Seq("x", "y")
    val response = PageResponse(pageRequest, 20, elements)
    
    assertEquals(response.page, 1)
    assertEquals(response.size, 10)
    assertEquals(response.total, 20)
    assertEquals(response.sort, None)
    assertEquals(response.elements, elements)
  }
}