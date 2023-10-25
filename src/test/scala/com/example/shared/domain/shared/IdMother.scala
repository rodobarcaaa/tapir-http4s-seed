package com.example.shared.domain.shared

import com.example.shared.domain.common.Id

import java.util.UUID

object IdMother {
  def random: Id = Id(UUID.randomUUID())
}
