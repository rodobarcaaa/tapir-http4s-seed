package com.example.auth.infrastructure.codecs

import com.example.auth.domain._
import com.example.shared.infrastructure.circe.CommonCodecs

trait AuthCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.extras.semiauto._

  implicit val UserCodec: Codec[User] = deriveConfiguredCodec
  implicit val AuthTokenCodec: Codec[AuthToken] = deriveConfiguredCodec
  implicit val UserCreateRequestCodec: Codec[UserCreateRequest] = deriveConfiguredCodec
  implicit val UserLoginRequestCodec: Codec[UserLoginRequest] = deriveConfiguredCodec
  implicit val UserLoginResponseCodec: Codec[UserLoginResponse] = deriveConfiguredCodec
  implicit val UserInfoCodec: Codec[UserInfo] = deriveConfiguredCodec
  implicit val AuthenticatedUserCodec: Codec[AuthenticatedUser] = deriveConfiguredCodec
}