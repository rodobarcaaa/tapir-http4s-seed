package com.example.auth.application

import cats.effect.IO
import com.example.auth.domain.{AuthenticatedUser, User, UserCreateRequest, UserInfo, UserLoginRequest, UserLoginResponse}
import com.example.shared.domain.common.Id
import com.example.shared.infrastructure.http.Fail

trait AuthService {
  def register(request: UserCreateRequest): IO[UserLoginResponse]
  def login(request: UserLoginRequest): IO[UserLoginResponse]
  def validateToken(token: String): IO[Option[AuthenticatedUser]]
  def getUserInfo(userId: Id): IO[Option[UserInfo]]
}

class AuthServiceImpl(
    userRepository: UserRepository,
    passwordService: PasswordService,
    jwtService: JwtService
) extends AuthService {

  override def register(request: UserCreateRequest): IO[UserLoginResponse] = {
    for {
      existingUser <- userRepository.findByUsername(request.username)
      _            <- existingUser match {
                        case Some(_) => IO.raiseError(Fail.Conflict("Username already exists"))
                        case None    => IO.unit
                      }
      existingEmail <- userRepository.findByEmail(request.email)
      _             <- existingEmail match {
                         case Some(_) => IO.raiseError(Fail.Conflict("Email already exists"))
                         case None    => IO.unit
                       }
      hashedPassword <- passwordService.hashPassword(request.password)
      user           <- IO.pure(User.create(request.username, request.email, hashedPassword))
      savedUser      <- userRepository.save(user)
      token          <- jwtService.generateToken(savedUser)
      userInfo       = UserInfo.fromUser(savedUser)
    } yield UserLoginResponse(token, userInfo)
  }

  override def login(request: UserLoginRequest): IO[UserLoginResponse] = {
    for {
      user <- userRepository.findByUsername(request.username).flatMap {
                case Some(user) => IO.pure(user)
                case None       => IO.raiseError(Fail.Unauthorized("Invalid username or password"))
              }
      isValidPassword <- passwordService.verifyPassword(request.password, user.passwordHash)
      _               <- if (isValidPassword) IO.unit 
                         else IO.raiseError(Fail.Unauthorized("Invalid username or password"))
      token           <- jwtService.generateToken(user)
      userInfo        = UserInfo.fromUser(user)
    } yield UserLoginResponse(token, userInfo)
  }

  override def validateToken(token: String): IO[Option[AuthenticatedUser]] = {
    jwtService.validateToken(token)
  }

  override def getUserInfo(userId: Id): IO[Option[UserInfo]] = {
    userRepository.findById(userId).map(_.map(UserInfo.fromUser))
  }
}

trait UserRepository {
  def save(user: User): IO[User]
  def findById(id: Id): IO[Option[User]]
  def findByUsername(username: String): IO[Option[User]]
  def findByEmail(email: String): IO[Option[User]]
}