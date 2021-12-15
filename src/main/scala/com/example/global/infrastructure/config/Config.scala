package com.example.global.infrastructure.config

import com.example.shared.infrastucture.http.HttpConfig

/** Maps to the `application.conf` file. Configuration for all modules of the application.
  */
case class Config(
//                   db: DBConfig,
    api: HttpConfig
)
