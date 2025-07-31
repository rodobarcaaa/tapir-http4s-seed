package com.example.auth.infrastructure.codecs

import com.example.auth.domain._
import com.example.shared.infrastructure.circe.CommonCodecs

trait AuthCodecs extends CommonCodecs {
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val RoleCodec: Codec[Role] = Codec.from(
    Decoder[String].emap(s => Role.fromString(s).toRight(s"Invalid role: $s")),
    Encoder[String].contramap(Role.toString)
  )

  implicit val UserCodec: Codec[User]                           = deriveCodec
  implicit val AuthTokenCodec: Codec[AuthToken]                 = deriveCodec
  implicit val UserCreateRequestCodec: Codec[UserCreateRequest] = deriveCodec
  implicit val UserLoginRequestCodec: Codec[UserLoginRequest]   = deriveCodec
  implicit val UserLoginResponseCodec: Codec[UserLoginResponse] = deriveCodec
  implicit val UserInfoCodec: Codec[UserInfo]                   = deriveCodec
  implicit val AuthenticatedUserCodec: Codec[AuthenticatedUser] = deriveCodec
}
