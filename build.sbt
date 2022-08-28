val scala2Version = "2.13.8"
val scala3Version = "3.1.3"

val circeVersion      = "0.14.2"
val tapirVersion      = "1.0.5"
val prometheusVersion = "0.16.0"
val macwireVersion    = "2.5.8"

lazy val root = project
  .in(file("."))
  .settings(
    name                     := "tapir-http4s-seed",
    scalaVersion             := scala2Version,
    version                  := (ThisBuild / version).value,
    crossScalaVersions       := Seq(scala2Version, scala3Version),
    coverageExcludedFiles    := ".*Main.scala",
    coverageFailOnMinimum    := true,
    coverageMinimumStmtTotal := 75,
    packageOptions += Package.ManifestAttributes("Implementation-Version" -> (ThisBuild / version).value),
    releaseCommitMessage     := s"ci: bumps version to ${(ThisBuild / version).value}",
    releaseNextCommitMessage := s"ci: bumps version to ${(ThisBuild / version).value}",
    sonarUseExternalConfig   := true,
    libraryDependencies ++= Seq(
      // base
      "com.github.pureconfig"         %% "pureconfig"                % "0.17.1",
      "com.typesafe.scala-logging"    %% "scala-logging"             % "3.9.5",
      "ch.qos.logback"                 % "logback-classic"           % "1.2.11",
      "org.typelevel"                 %% "cats-core"                 % "2.8.0",
      "org.typelevel"                 %% "cats-effect"               % "3.3.14",
      // tapir
      "com.softwaremill.sttp.tapir"   %% "tapir-core"                % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-cats"                % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"          % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"       % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"        % tapirVersion,
      "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"   % tapirVersion,
      "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"        % "0.2.1",
      // circe
      "io.circe"                      %% "circe-core"                % circeVersion,
      "io.circe"                      %% "circe-generic"             % circeVersion,
      "io.circe"                      %% "circe-generic-extras"      % circeVersion,
      "io.circe"                      %% "circe-parser"              % circeVersion,
      // http4s
      "org.http4s"                    %% "http4s-core"               % "0.23.14",
      "org.http4s"                    %% "http4s-dsl"                % "0.23.14",
      "org.http4s"                    %% "http4s-circe"              % "0.23.14",
      "org.http4s"                    %% "http4s-blaze-server"       % "0.23.12",
      "org.http4s"                    %% "http4s-blaze-client"       % "0.23.12",
      "org.http4s"                    %% "http4s-prometheus-metrics" % "0.24.1",
      // prometheus
      "io.prometheus"                  % "simpleclient"              % prometheusVersion,
      "io.prometheus"                  % "simpleclient_hotspot"      % prometheusVersion,
      // macwire
      "com.softwaremill.macwire"      %% "util"                      % macwireVersion,
      "com.softwaremill.macwire"      %% "macros"                    % macwireVersion % Provided,
      // munit
      "org.scalameta"                 %% "munit"                     % "0.7.29"       % Test,
      "org.typelevel"                 %% "munit-cats-effect-3"       % "1.0.7"        % Test,
      "com.alejandrohdezma"           %% "http4s-munit"              % "0.9.3"        % Test
    ),
    testFrameworks += new TestFramework("munit.Framework")
  )

addCommandAlias("f", "scalafmt")
addCommandAlias("fc", "scalafmtCheck")
addCommandAlias("tf", "test:scalafmt")
addCommandAlias("tfc", "test:scalafmtCheck")
addCommandAlias("fmt", ";f;tf")
addCommandAlias("fmtCheck", ";fc;tfc")

addCommandAlias("sonar", ";clean;coverage;test;coverageReport;sonarScan")
