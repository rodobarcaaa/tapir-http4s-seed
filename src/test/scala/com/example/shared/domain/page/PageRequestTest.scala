package com.example.shared.domain.page

import munit.FunSuite

class PageRequestTest extends FunSuite {

  test("PageRequest should create valid request with page and size") {
    val pageRequest = PageRequest(1, 10)
    assertEquals(pageRequest.page, 1)
    assertEquals(pageRequest.size, 10)
    assertEquals(pageRequest.sort, None)
    assertEquals(pageRequest.offset, 0)
  }

  test("PageRequest should create valid request with sort") {
    val pageRequest = PageRequest(2, 20, Some("name"))
    assertEquals(pageRequest.page, 2)
    assertEquals(pageRequest.size, 20)
    assertEquals(pageRequest.sort, Some("name"))
    assertEquals(pageRequest.offset, 20)
  }

  test("PageRequest should calculate offset correctly") {
    val page1 = PageRequest(1, 10)
    assertEquals(page1.offset, 0)

    val page2 = PageRequest(2, 10)
    assertEquals(page2.offset, 10)

    val page3 = PageRequest(3, 15)
    assertEquals(page3.offset, 30)
  }

  test("PageRequest should fail for page <= 0") {
    interceptMessage[IllegalArgumentException]("requirement failed: page must be greater than 0") {
      PageRequest(0, 10)
    }

    interceptMessage[IllegalArgumentException]("requirement failed: page must be greater than 0") {
      PageRequest(-1, 10)
    }
  }

  test("PageRequest should fail for size > MAX_PAGE_SIZE") {
    interceptMessage[IllegalArgumentException]("requirement failed: size must be less or equal to 100") {
      PageRequest(1, PageRequest.MAX_PAGE_SIZE + 1)
    }
  }

  test("PageRequest should accept size == MAX_PAGE_SIZE") {
    val pageRequest = PageRequest(1, PageRequest.MAX_PAGE_SIZE)
    assertEquals(pageRequest.size, PageRequest.MAX_PAGE_SIZE)
  }

  test("PageRequest MAX_PAGE_SIZE should be 100") {
    assertEquals(PageRequest.MAX_PAGE_SIZE, 100)
  }
}
