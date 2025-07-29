package com.example.auth.infrastructure.codecs

import com.example.auth.domain._
import com.example.shared.infrastructure.circe.CommonCodecs
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

trait AuthCodecs extends CommonCodecs {
  implicit val loginRequestDecoder: Decoder[LoginRequest] = deriveDecoder[LoginRequest]
  implicit val loginRequestEncoder: Encoder[LoginRequest] = deriveEncoder[LoginRequest]
  
  implicit val userInfoDecoder: Decoder[UserInfo] = deriveDecoder[UserInfo]
  implicit val userInfoEncoder: Encoder[UserInfo] = deriveEncoder[UserInfo]
  
  implicit val loginResponseDecoder: Decoder[LoginResponse] = deriveDecoder[LoginResponse]
  implicit val loginResponseEncoder: Encoder[LoginResponse] = deriveEncoder[LoginResponse]
}