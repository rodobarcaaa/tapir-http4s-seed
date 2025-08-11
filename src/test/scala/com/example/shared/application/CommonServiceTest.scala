package com.example.shared.application

import cats.data.ValidatedNel
import cats.effect.IO
import com.example.shared.domain.common.HasValidated
import com.example.shared.infrastructure.http.Fail
import munit.CatsEffectSuite

class CommonServiceTest extends CatsEffectSuite {

  private val commonService = new CommonService

  // Test object that implements HasValidated
  case class TestRequest(value: String) extends HasValidated {
    override def validated: ValidatedNel[String, Unit] = {
      if (value.nonEmpty) cats.data.Validated.validNel(())
      else cats.data.Validated.invalidNel("value cannot be empty")
    }
  }

  test("validateRequest should succeed with valid request") {
    val validRequest = TestRequest("valid value")
    for {
      result <- commonService.validateRequest(validRequest)
    } yield {
      // Should complete without error
      assertEquals(result, ())
    }
  }

  test("validateRequest should fail with invalid request") {
    val invalidRequest = TestRequest("")
    for {
      result <- commonService.validateRequest(invalidRequest).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.UnprocessableEntity])
        val failError = error.asInstanceOf[Fail.UnprocessableEntity]
        assert(failError.msg.contains("value cannot be empty"))
      }
    }
  }

  test("validateRequest should fail with multiple validation errors") {
    val requestWithMultipleErrors = new HasValidated {
      override def validated: ValidatedNel[String, Unit] = {
        cats.data.Validated.invalid(
          cats.data.NonEmptyList.of(
            "error 1",
            "error 2",
            "error 3"
          )
        )
      }
    }
    for {
      result <- commonService.validateRequest(requestWithMultipleErrors).attempt
    } yield {
      assert(result.isLeft)
      result.left.foreach { error =>
        assert(error.isInstanceOf[Fail.UnprocessableEntity])
        val failError = error.asInstanceOf[Fail.UnprocessableEntity]
        assert(failError.msg.contains("error 1"))
        assert(failError.msg.contains("error 2"))
        assert(failError.msg.contains("error 3"))
      }
    }
  }
}
