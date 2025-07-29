package com.example.shared.infrastructure.slick

import slick.jdbc.PostgresProfile

// Simplified profile for Scala 3 compatibility without slick-pg
trait PgProfile extends PostgresProfile {
  // Use the default API from PostgresProfile for now
}

object PgProfile extends PgProfile
