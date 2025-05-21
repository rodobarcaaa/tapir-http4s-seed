import com.typesafe.sbt.packager.docker.*
import sbt.Test
import sbtrelease.ReleaseStateTransformations.*

import scala.language.postfixOps

val scala2Version = "2.13.16"
val scala3Version = "3.7.0"

val dependencies = {
  val circeVersion      = "0.14.3"
  val tapirVersion      = "1.1.2"
  val http4sVersion     = "0.23.30"
  val prometheusVersion = "0.16.0"
  val macwireVersion    = "2.6.4"
  val slickPgVersion    = "0.21.1"

  Seq(
    // base
    "com.github.pureconfig"         %% "pureconfig"                % "0.17.4",
    "com.typesafe.scala-logging"    %% "scala-logging"             % "3.9.5",
    "ch.qos.logback"                 % "logback-classic"           % "1.5.15",
    "org.typelevel"                 %% "cats-core"                 % "2.12.0",
    "org.typelevel"                 %% "cats-effect"               % "3.5.7",
    // tapir
    "com.softwaremill.sttp.tapir"   %% "tapir-core"                % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-cats"                % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-json-circe"          % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-http4s-server"       % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-openapi-docs"        % tapirVersion,
    "com.softwaremill.sttp.tapir"   %% "tapir-swagger-ui-bundle"   % tapirVersion,
    "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml"        % "0.2.1",
    "com.alejandrohdezma"           %% "tapir-anyof"               % "0.5.1",
    // circe
    "io.circe"                      %% "circe-core"                % circeVersion,
    "io.circe"                      %% "circe-generic"             % circeVersion,
    "io.circe"                      %% "circe-generic-extras"      % circeVersion,
    "io.circe"                      %% "circe-parser"              % circeVersion,
    // http4s
    "org.http4s"                    %% "http4s-core"               % http4sVersion,
    "org.http4s"                    %% "http4s-dsl"                % http4sVersion,
    "org.http4s"                    %% "http4s-circe"              % http4sVersion,
    "org.http4s"                    %% "http4s-blaze-server"       % "0.23.16",
    "org.http4s"                    %% "http4s-blaze-client"       % "0.23.16",
    "org.http4s"                    %% "http4s-prometheus-metrics" % "0.25.0",
    // slick-pg
    "com.github.tminglei"           %% "slick-pg"                  % slickPgVersion,
    "com.github.tminglei"           %% "slick-pg_circe-json"       % slickPgVersion,
    // flyway 4 scala
    "com.github.geirolz"            %% "fly4s-core"                % "0.0.20",
    // prometheus
    "io.prometheus"                  % "simpleclient"              % prometheusVersion,
    "io.prometheus"                  % "simpleclient_hotspot"      % prometheusVersion,
    // macwire
    "com.softwaremill.macwire"      %% "util"                      % macwireVersion,
    "com.softwaremill.macwire"      %% "macros"                    % macwireVersion % Provided,
    // munit
    "org.scalameta"                 %% "munit"                     % "1.0.3"        % Test,
    "org.typelevel"                 %% "munit-cats-effect"         % "2.0.0"        % Test,
    "com.alejandrohdezma"           %% "http4s-munit"              % "0.15.1"       % Test
  )
}

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    name                        := "tapir-http4s-seed",
    scalaVersion                := scala2Version,
    crossScalaVersions          := Seq(scala2Version, scala3Version),
    version                     := (ThisBuild / version).value,
    scalacOptions ++= Seq(
      "-encoding",
      "utf-8",                         // Specify character encoding used by source files.
      "-explaintypes",                 // Explain type errors in more detail.
      "-feature",                      // Emit warning and location for usages of features that should be imported explicitly.
      "-unchecked",                    // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit",                   // Wrap field accessors to throw an exception on uninitialized access.
      "-Ymacro-annotations",           // Enable support for macro annotations, formerly in macro paradise.
      // ********** -language: Enable or disable language features *****************
      "-language:dynamics",            // Allow direct or indirect subclasses of scala.Dynamic
      "-language:existentials",        // Existential types (besides wildcard types) can be written and inferred
      "-language:higherKinds",         // Allow higher-kinded types
      "-language:postfixOps",          // Allow postfix operator notation, such as 1 to 10 toList (not recommended)
      "-language:reflectiveCalls",     // Allow reflective access to members of structural types
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      // ********** Warning Settings ***********************************************
      "-Werror",                       // Fail the compilation if there are any warnings.
      "-Wdead-code",                   // Warn when dead code is identified.
      "-Wextra-implicit",              // Warn when more than one implicit parameter section is defined.
      "-Wmacros:none",                 // Do not inspect expansions or their original trees when generating unused symbol warnings.
      "-Wmacros:before",               // Only inspect unexpanded user-written code for unused symbols. (Default)
      "-Wmacros:after",                // Only inspect expanded trees when generating unused symbol warnings.
      "-Wmacros:both",                 // Inspect both user-written code and expanded trees when generating unused symbol warnings.
      "-Wnumeric-widen",               // Warn when numerics are widened.
      "-Woctal-literal",               // Warn on obsolete octal syntax.
      "-Wunused:imports",              // Warn if an import selector is not referenced.
      "-Wunused:patvars",              // Warn if a variable bound in a pattern is unused.
      "-Wunused:privates",             // Warn if a private member is unused.
      "-Wunused:locals",               // Warn if a local definition is unused.
      "-Wunused:explicits",            // Warn if an explicit parameter is unused.
      "-Wunused:implicits",            // Warn if an implicit parameter is unused.
      "-Wunused:params",               // Enable -Wunused:explicits,implicits.
      "-Wvalue-discard",               // Warn when non-Unit expression results are unused.
      // ********** -Xlint: Enable recommended warnings ****************************
      "-Xlint:adapted-args",           // Warn if an argument list is modified to match the receiver.
      "-Xlint:nullary-unit",           // Warn when nullary methods return Unit.
      "-Xlint:inaccessible",           // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any",              // Warn when a type argument is inferred to be Any.
      "-Xlint:missing-interpolator",   // A string literal appears to be missing an interpolator id.
      "-Xlint:doc-detached",           // A Scaladoc comment appears to be detached from its element.
      "-Xlint:private-shadow",         // A private field (or class parameter) shadows a superclass field.
      "-Xlint:type-parameter-shadow",  // A local type parameter shadows a type already in scope.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:option-implicit",        // Option.apply used implicit view.
      "-Xlint:delayedinit-select",     // Selecting member of DelayedInit.
      "-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:stars-align",            // Pattern sequence wildcard must align with sequence component.
      "-Xlint:constant",               // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:nonlocal-return",        // A return statement used an exception for flow control.
      "-Xlint:implicit-not-found",     // Check @implicitNotFound and @implicitAmbiguous messages.
      "-Xlint:serial",                 // @SerialVersionUID on traits and non-serializable classes.
      "-Xlint:valpattern",             // Enable pattern checks in val definitions.
      "-Xlint:eta-zero",               // Warn on eta-expansion (rather than auto-application) of zero-ary method.
      "-Xlint:eta-sam",                // Warn on eta-expansion to meet a Java-defined functional interface that is not explicitly annotated with @FunctionalInterface.
      "-Xlint:deprecation",            // Enable linted deprecations.
      "-Xlint:implicit-recursion",     // Warn when an implicit resolves to an enclosing self-definition.
      "-Xfatal-warnings"               // Fail the compilation if there are any warnings.
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
