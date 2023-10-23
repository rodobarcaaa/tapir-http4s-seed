package com.example.shared.infrastructure.config

import com.example.global.infrastructure.http.{DBConfig, HttpConfig}

/** Maps to the `application.conf` file. Configuration for all modules of the application.
  */
case class Config(
    db: DBConfig,
    api: HttpConfig
)
