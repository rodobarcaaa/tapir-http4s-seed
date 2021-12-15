package com.example

import com.example.global.infrastructure.config.ConfigModule
import io.prometheus.client.hotspot

/** Initialised resources needed by the application to start.
  */
trait InitModule extends ConfigModule {

  def initialize(): Unit = {
    loadConfig()
    hotspot.DefaultExports.initialize()
  }

}
