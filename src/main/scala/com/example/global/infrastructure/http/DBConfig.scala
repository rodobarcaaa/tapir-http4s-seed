package com.example.global.infrastructure.http

import com.example.shared.infrastructure.config.Sensitive

case class DBConfig(
    driver: String,
    url: String,
    user: String,
    password: Sensitive
)
