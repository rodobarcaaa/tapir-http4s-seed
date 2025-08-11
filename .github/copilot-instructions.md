# Copilot Instructions for tapir-http4s-seed

This repository is a Scala 3 project using the following technologies:
- **tapir** - Functional API endpoints with automatic OpenAPI documentation
- **http4s** - Functional HTTP server
- **Doobie** - Functional database access
- **Cats Effect** - Functional effect management
- **PostgreSQL** - Database

## Code Style and Formatting

- Always run `sbt scalafmt` to format code before committing/pushing changes
- The project uses scalafmt with configuration in `.scalafmt.conf`
- Follow functional programming principles and immutable data structures
- Use Scala 3 syntax and features where appropriate

## Testing

- Use munit testing framework for all tests
- Maintain test coverage above 85%
- Write unit tests for service layer, domain models, and shared utilities
- Use "Mother" objects for test data generation
- Test both positive and negative scenarios

## Project Structure

- `src/main/scala/com/example/`
  - `auth/` - Authentication and authorization
  - `books/` - Book management domain
  - `authors/` - Author management domain
  - `publishers/` - Publisher management domain
  - `shared/` - Shared utilities and domain objects
- `src/test/scala/com/example/` - Mirrors main structure for tests

## Development Workflow

1. Always format code with `sbt scalafmt` before pushing
2. Run tests with `sbt test`
3. Check coverage with `sbt coverage test coverageReport`
4. Ensure all tests pass and coverage meets minimum thresholds
5. Follow the existing patterns for error handling and validation

## Database

- Uses PostgreSQL with Docker for local development
- Doobie for functional database access
- Database migrations handled through initialization scripts
- Connection pooling and transaction management through Cats Effect

## API Documentation

- tapir automatically generates OpenAPI documentation
- Endpoints are defined using tapir's functional DSL
- JSON serialization/deserialization using circe
