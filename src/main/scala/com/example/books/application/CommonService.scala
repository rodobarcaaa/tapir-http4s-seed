package com.example.books.application

import cats.data.ValidatedNel
import cats.effect.IO
import com.example.books.domain.common.HasValidated
import com.example.shared.infrastructure.http.Fail

class CommonService {

  def validateRequest(req: HasValidated): IO[Unit] = {
    checkValidations(req.validated)
  }

  def checkValidations(value: ValidatedNel[String, Unit]): IO[Unit] = {
    val errors = value.fold(_.toList, _ => Nil)
    IO.raiseWhen(errors.nonEmpty)(Fail.UnprocessableEntity(errors.mkString(", ")))
  }

}
