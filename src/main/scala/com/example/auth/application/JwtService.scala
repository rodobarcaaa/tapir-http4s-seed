package com.example.auth.application

import cats.effect.{Clock, IO}
import com.example.auth.domain.{AuthenticatedUser, User, UserInfo}
import com.example.auth.infrastructure.codecs.AuthCodecs
import io.circe.parser.decode
import io.circe.syntax._
import pdi.jwt.{JwtAlgorithm, JwtCirce, JwtClaim}

import java.time.Instant
import java.util.UUID
import scala.concurrent.duration._
import scala.util.{Failure, Success}

trait JwtService {
  def generateToken(user: User): IO[String]
  def validateToken(token: String): IO[Option[AuthenticatedUser]]
  def getUserFromToken(token: String): IO[Option[UserInfo]]
}

class JwtServiceImpl(secret: String, tokenExpiration: FiniteDuration = 24.hours) extends JwtService with AuthCodecs {

  private val algorithm = JwtAlgorithm.HS256

  override def generateToken(user: User): IO[String] = {
    for {
      now <- Clock[IO].realTimeInstant
      exp = now.plusSeconds(tokenExpiration.toSeconds)
      userInfo = UserInfo.fromUser(user)
      claim = JwtClaim(
        content = userInfo.asJson.noSpaces,
        issuer = Some("tapir-http4s-seed"),
        subject = Some(user.id.value.toString),
        audience = None,
        expiration = Some(exp.getEpochSecond),
        notBefore = Some(now.getEpochSecond),
        issuedAt = Some(now.getEpochSecond),
        jwtId = Some(UUID.randomUUID().toString)
      )
      token = JwtCirce.encode(claim, secret, algorithm)
    } yield token
  }

  override def validateToken(token: String): IO[Option[AuthenticatedUser]] = {
    IO {
      JwtCirce.decode(token, secret, Seq(algorithm)) match {
        case Success(claim) =>
          val now = Instant.now().getEpochSecond
          if (claim.expiration.exists(_ > now)) {
            decode[UserInfo](claim.content) match {
              case Right(userInfo) => Some(AuthenticatedUser(userInfo, token))
              case Left(_) => None
            }
          } else {
            None
          }
        case Failure(_) => None
      }
    }
  }

  override def getUserFromToken(token: String): IO[Option[UserInfo]] = {
    validateToken(token).map(_.map(_.user))
  }
}

object JwtService {
  def apply(secret: String, tokenExpiration: FiniteDuration = 24.hours): JwtService =
    new JwtServiceImpl(secret, tokenExpiration)
}