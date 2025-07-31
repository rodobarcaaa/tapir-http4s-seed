import com.typesafe.sbt.packager.docker.*
import sbt.Test
import sbtrelease.ReleaseStateTransformations.*

import scala.language.postfixOps

val scala3Version = "3.7.0"

val dependencies = {
  val circeVersion              = "0.14.10"
  val tapirVersion              = "1.11.9"
  val http4sVersion             = "0.23.30"
  val http4sBlazeVersion        = "0.23.16"
  val http4sPrometheusVersion   = "0.25.0"
  val prometheusVersion         = "0.16.0"
  val macwireVersion            = "2.6.4"
  val jwtVersion                = "10.0.1"
  val configVersion             = "1.4.3"
  val scalaLoggingVersion       = "3.9.5"
  val logbackVersion            = "1.5.15"
  val catsVersion               = "2.12.0"
  val catsEffectVersion         = "3.6.3"
  val openApiVersion            = "0.11.3"
  val slickVersion              = "3.5.2"
  val postgresqlVersion         = "42.7.4"
  val fly4sVersion              = "0.0.20"
  val bcryptVersion             = "0.4"
  val munitVersion              = "1.0.3"
  val munitCatsEffectVersion    = "2.0.0"
  val http4sMunitVersion        = "0.15.1"

  Seq(
    // base - Use alternative to pureconfig for now
    "com.typesafe"                  % "config"                    % configVersion,
    "com.typesafe.scala-logging"    %% "scala-logging"             % scalaLoggingVersion,
    "ch.qos.logback"                 % "logback-classic"           % logbackVersion,
    "org.typelevel"                 %% "cats-core"                 % catsVersion,
    "org.typelevel"                 %% "cats-effect"               % catsEffectVersion,
    // tapir
    "com.softwaremill.sttp.tapir"   %% "tapir-core"                % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-cats"                % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"          % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"       % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"        % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"   % tapirVersion,
    "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"        % openApiVersion,
    // circe
    "io.circe"                      %% "circe-core"                % circeVersion,
    "io.circe"                      %% "circe-generic"             % circeVersion,
    "io.circe"                      %% "circe-parser"              % circeVersion,
    // http4s
    "org.http4s"                    %% "http4s-core"               % http4sVersion,
    "org.http4s"                    %% "http4s-dsl"                % http4sVersion,
    "org.http4s"                    %% "http4s-circe"              % http4sVersion,
    "org.http4s"                    %% "http4s-blaze-server"       % http4sBlazeVersion,
    "org.http4s"                    %% "http4s-blaze-client"       % http4sBlazeVersion,
    "org.http4s"                    %% "http4s-prometheus-metrics" % http4sPrometheusVersion,
    // slick
    "com.typesafe.slick"            %% "slick"                     % slickVersion,
    "com.typesafe.slick"            %% "slick-hikaricp"            % slickVersion,
    "org.postgresql"                 % "postgresql"                % postgresqlVersion,
    // flyway 4 scala
    "com.github.geirolz"            %% "fly4s-core"                % fly4sVersion,
    // prometheus
    "io.prometheus"                  % "simpleclient"              % prometheusVersion,
    "io.prometheus"                  % "simpleclient_hotspot"      % prometheusVersion,
    // macwire
    "com.softwaremill.macwire"      %% "util"                      % macwireVersion,
    "com.softwaremill.macwire"      %% "macros"                    % macwireVersion % Provided,
    // jwt authentication
    "com.github.jwt-scala"          %% "jwt-core"                  % jwtVersion,
    "com.github.jwt-scala"          %% "jwt-circe"                 % jwtVersion,
    "org.mindrot"                    % "jbcrypt"                   % bcryptVersion,
    // munit
    "org.scalameta"                 %% "munit"                     % munitVersion        % Test,
    "org.typelevel"                 %% "munit-cats-effect"         % munitCatsEffectVersion        % Test,
    "com.alejandrohdezma"           %% "http4s-munit"              % http4sMunitVersion       % Test
  )
}

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    name                        := "tapir-http4s-seed",
    scalaVersion                := scala3Version,
    version                     := (ThisBuild / version).value,
    scalacOptions ++= Seq(
      "-encoding",
      "utf-8",                         // Specify character encoding used by source files.
      "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
      "-deprecation",                  // Emit warning and location for usages of deprecated APIs.
      // ********** -language: Enable or disable language features *****************
      "-language:dynamics",            // Allow direct or indirect subclasses of scala.Dynamic
      "-language:postfixOps",          // Allow postfix operator notation, such as 1 to 10 toList (not recommended)
      "-language:reflectiveCalls",     // Allow reflective access to members of structural types
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      // ********** Warning Settings ***********************************************
      // Temporarily disabling -Werror to get compilation working
      // "-Werror",                       // Fail the compilation if there are any warnings.
      "-Wunused:imports",              // Warn if an import selector is not referenced.
      "-Wunused:privates",             // Warn if a private member is unused.
      "-Wunused:locals",               // Warn if a local definition is unused.
      "-Wunused:explicits",            // Warn if an explicit parameter is unused.
      "-Wunused:implicits",            // Warn if an implicit parameter is unused.
      "-Wunused:params",               // Enable -Wunused:explicits,implicits.
      "-Wvalue-discard"                // Warn when non-Unit expression results are unused.
    ),
    coverageExcludedFiles       := ".*Main.scala",
    coverageFailOnMinimum       := true,
    coverageMinimumStmtTotal    := 75,
    packageOptions += Package.ManifestAttributes("Implementation-Version" -> (ThisBuild / version).value),
    releaseCommitMessage        := s"ci: bumps version to ${(ThisBuild / version).value}",
    releaseNextCommitMessage    := s"ci: bumps version to ${(ThisBuild / version).value}",
    releaseIgnoreUntrackedFiles := true,
    releaseProcess              := Seq[ReleaseStep](
      checkSnapshotDependencies,
      inquireVersions,
      setReleaseVersion,
      commitReleaseVersion,
      tagRelease,
      setNextVersion,
      commitNextVersion,
      pushChanges
    ),
    sonarUseExternalConfig      := true,
    libraryDependencies ++= dependencies,
    testFrameworks += new TestFramework("munit.Framework")
  )

//********* DOCKER IMAGE *********
Universal / javaOptions ++= Seq(
  // JVM memory tuning - https://www.scala-sbt.org/sbt-native-packager/recipes/play.html#build-configuration
  "-J-server",
  "-J-XX:+UseG1GC",
  "-J-XX:MaxPermSize",
  "-J-XX:+TieredCompilation",
  "-J-XX:+ExitOnOutOfMemoryError",
  "-J-XX:+CrashOnOutOfMemoryError",
  "-Dpidfile.path=/dev/null",
  "-Dfile.encoding=UTF-8"
)

lazy val repository = sys.props.getOrElse("repository", "734120256922.dkr.ecr.us-east-1.amazonaws.com")

Compile / mainClass    := Some("com.example.Main")
Docker / packageName   := repository + "/bookstore"
Docker / maintainer    := "rodo@echemendía.com"
Docker / daemonUserUid := None
Docker / daemonUser    := "daemon"

dockerBaseImage      := "openjdk:11-jre"
dockerExposedPorts   := Seq(8080)
dockerExposedVolumes := Seq("/opt/docker/logs")
dockerChmodType      := DockerChmodType.UserGroupWriteExecute
dockerAliases ++= Seq(dockerAlias.value.withTag(sys.props.get("environment")))
dockerUpdateLatest   := true

//********* COMMANDS ALIASES *********
addCommandAlias("f", "scalafmt")
addCommandAlias("fc", "scalafmtCheck")
addCommandAlias("tf", "test:scalafmt")
addCommandAlias("tfc", "test:scalafmtCheck")
addCommandAlias("fmt", ";f;tf")
addCommandAlias("fmtCheck", ";fc;tfc")

addCommandAlias("tcoverage", ";coverage;test;coverageReport")
addCommandAlias("sonar", ";clean;coverage;test;coverageReport;sonarScan")
