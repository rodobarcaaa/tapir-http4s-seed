package com.example.auth.domain

import com.example.shared.domain.common.Id

case class User(
    id: Id,
    username: String,
    email: String,
    passwordHash: String
)

case class LoginRequest(
    username: String,
    password: String
)

case class AuthToken(
    token: String,
    userId: Id
)

case class LoginResponse(
    token: String,
    user: UserInfo
)

case class UserInfo(
    id: Id,
    username: String,
    email: String
)