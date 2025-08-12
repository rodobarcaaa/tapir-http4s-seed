l # CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Scala 3 project using functional programming with tapir for API endpoints, http4s for HTTP server, Doobie for database access, and Cats Effect for effect management. The application is a bookstore API with authentication, built following Domain-Driven Design principles.

## Development Commands

### Build and Run
- Start the application: `sbt run`
- Start dependencies: `docker-compose up -d`
- Recreate database: `./recreate-db`

### Testing
- Run all tests: `sbt test`
- Run tests with coverage: `sbt tcoverage`
- Run single test suite: `sbt "testOnly *SomeTest"`
- Coverage minimum: 75% (configured in build.sbt)

### Code Quality
- Format code: `sbt scalafmt` (alias: `sbt f`)
- Format test code: `sbt test:scalafmt` (alias: `sbt tf`)
- Format all: `sbt fmt`
- Check formatting: `sbt fmtCheck`
- Run SonarQube analysis: `sbt sonar`

### Build Commands Available
- `sbt compile` - Compile main sources
- `sbt test:compile` - Compile test sources
- `sbt docker:publishLocal` - Build Docker image locally

## Architecture

### Domain Structure
The codebase follows Domain-Driven Design with clear separation:

- **Domain Layer**: Core business logic, entities, and repository interfaces
  - `domain/` - Contains domain models, value objects, and repository traits
- **Application Layer**: Use cases and services that orchestrate domain logic
  - `application/` - Contains service classes that coordinate domain operations
- **Infrastructure Layer**: Technical implementations, database, HTTP, etc.
  - `infrastructure/http/` - API endpoints using tapir
  - `infrastructure/repository/` - Database implementations using Slick
  - `infrastructure/codecs/` - JSON serialization with circe

### Module Organization
- `auth/` - JWT-based authentication system
- `books/` - Book management domain (includes authors and publishers)
- `shared/` - Common utilities, pagination, validation
- `global/` - Application-wide infrastructure (DB config, metrics)

### Key Technologies
- **tapir**: Functional API definition with automatic OpenAPI docs
- **http4s**: HTTP server with functional routes
- **Slick**: Functional database access with PostgreSQL
- **Cats Effect**: Effect management and resource handling
- **Macwire**: Compile-time dependency injection
- **circe**: JSON encoding/decoding
- **MUnit**: Testing framework

### Authentication
JWT-based authentication is implemented with:
- User registration and login endpoints at `/auth/`
- Token validation for protected endpoints
- BCrypt password hashing
- Role-based authorization framework

## Important Patterns

### Repository Pattern
All data access follows the repository pattern:
```scala
trait SomeRepository {
  def findById(id: Id): IO[Option[Entity]]
  def save(entity: Entity): IO[Entity]
}
```

### Service Layer
Services coordinate domain operations and handle business logic:
```scala
class SomeService(repository: SomeRepository) {
  def businessOperation(input: Input): IO[Result] = ...
}
```

### API Layer
APIs are defined using tapir's functional DSL:
```scala
trait SomeApi extends HasTapirEndpoints {
  val endpoints: ServerEndpoints = List(endpoint1, endpoint2)
}
```

### Error Handling
Use the `Fail` types for consistent error responses across APIs.

### Pagination
Implemented through `PageRequest` and `PageResponse` in the shared domain.

## Testing Strategy

- Use MUnit testing framework
- Follow "Mother" pattern for test data generation
- Test coverage required above 75%
- Test both positive and negative scenarios
- Unit tests for services, integration tests for repositories
- HTTP tests using http4s-munit

## Database

- PostgreSQL database with Docker for local development
- Slick for functional database access with custom PostgreSQL profile
- Database initialization through Docker compose
- No formal migration system - uses initialization scripts

## Configuration

- Configuration handled through Typesafe Config
- Environment-specific settings in application.conf
- Database and HTTP server configuration centralized

## Deployment

- Docker support with sbt-native-packager
- Base image: openjdk:11-jre
- Exposed port: 8080
- Health endpoints: `/metrics` for Prometheus monitoring
- API documentation: `/docs/` for Swagger UI