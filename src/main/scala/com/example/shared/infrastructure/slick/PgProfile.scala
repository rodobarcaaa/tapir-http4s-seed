package com.example.shared.infrastructure.slick

import slick.jdbc.PostgresProfile

trait PgProfile extends PostgresProfile

object PgProfile extends PgProfile
