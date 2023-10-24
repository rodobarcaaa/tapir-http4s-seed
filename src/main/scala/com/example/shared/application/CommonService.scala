package com.example.shared.application

import cats.effect.IO
import com.example.shared.domain.common.HasValidated
import com.example.shared.infrastructure.http.Fail

class CommonService {

  def validateRequest(req: HasValidated): IO[Unit] = {
    val errors = req.validated.fold(_.toList, _ => Nil)
    IO.raiseWhen(errors.nonEmpty)(Fail.UnprocessableEntity(errors.mkString(", ")))
  }

}
