# tapir-http4s-seed

[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tapir-http4s-seed&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=tapir-http4s-seed)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=tapir-http4s-seed&metric=coverage)](https://sonarcloud.io/summary/new_code?id=tapir-http4s-seed)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=tapir-http4s-seed&metric=bugs)](https://sonarcloud.io/summary/new_code?id=tapir-http4s-seed)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=tapir-http4s-seed&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=tapir-http4s-seed)

## Try it! JDK 21

```
docker-compose up -d
sbt run
```

Swagger: http://0.0.0.0:8080/docs/

Metrics: http://0.0.0.0:8080/metrics

## Tests run

```
sh recreate-db
```

- Run all tests: `sbt test`
- Run all tests with coverage check: `sbt tcoverage`
- Run all tests with coverage check and formatting: `sbt scalafmtAll tcoverage`

Also, you can run only one suite like this

```
sbt "to *SOMETest"
```

## Ref Documentation

- Http4s: https://http4s.org/v0.23/docs/quickstart.html
- Tapir: https://tapir.softwaremill.com/en/latest/quickstart.html
- Macwire: https://github.com/softwaremill/macwire
- Circe: https://circe.github.io/circe/
- Cats Core: https://typelevel.org/cats/
- Cats Effect: https://typelevel.org/cats-effect/
- Slick: http://books.underscore.io/essential-slick/essential-slick-3.html
- Slick-Pg: https://github.com/tminglei/slick-pg
- Fly4s: https://geirolz.github.io/fly4s/
- MUnit: https://scalameta.org/munit/docs/getting-started.html
- Sonar Scala: https://sonar-scala.com/

