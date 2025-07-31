package com.example.auth.application

import cats.effect.IO
import com.example.auth.domain.{
  AuthenticatedUser,
  Role,
  User,
  UserCreateRequest,
  UserInfo,
  UserLoginRequest,
  UserLoginResponse,
  UserRepository
}
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.Fail

final class AuthService(
    userRepository: UserRepository,
    passwordRepository: PasswordRepository,
    jwtRepository: JwtRepository
) {

  def register(request: UserCreateRequest): IO[UserLoginResponse] = {
    for {
      existingUser   <- userRepository.findByUsername(request.username)
      _              <- existingUser match {
                          case Some(_) => IO.raiseError(Fail.Conflict("Username already exists"))
                          case None    => IO.unit
                        }
      existingEmail  <- userRepository.findByEmail(request.email)
      _              <- existingEmail match {
                          case Some(_) => IO.raiseError(Fail.Conflict("Email already exists"))
                          case None    => IO.unit
                        }
      hashedPassword <- passwordRepository.hashPassword(request.password)
      role            = request.role.getOrElse(Role.Customer)
      user           <- IO.pure(User.create(request.username, request.email, hashedPassword, role))
      savedUser      <- userRepository.save(user)
      token          <- jwtRepository.generateToken(savedUser)
      userInfo        = UserInfo.fromUser(savedUser)
    } yield UserLoginResponse(token, userInfo)
  }

  def login(request: UserLoginRequest): IO[UserLoginResponse] = {
    for {
      user            <- userRepository.findByUsername(request.username).flatMap {
                           case Some(user) => IO.pure(user)
                           case None       => IO.raiseError(Fail.Unauthorized("Invalid username or password"))
                         }
      isValidPassword <- passwordRepository.verifyPassword(request.password, user.passwordHash)
      _               <- if (isValidPassword) IO.unit
                         else IO.raiseError(Fail.Unauthorized("Invalid username or password"))
      token           <- jwtRepository.generateToken(user)
      userInfo         = UserInfo.fromUser(user)
    } yield UserLoginResponse(token, userInfo)
  }

  def validateToken(token: String): IO[Option[AuthenticatedUser]] = {
    jwtRepository.validateToken(token)
  }

  def getUserInfo(userId: Id): IO[Option[UserInfo]] = {
    userRepository.findById(userId).map(_.map(UserInfo.fromUser))
  }
}
