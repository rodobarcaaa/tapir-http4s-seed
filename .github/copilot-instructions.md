# Finance API Scala - GitHub Copilot Instructions

**ALWAYS follow these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Bootstrap, Build, and Test the Repository

**Prerequisites Installation:**
```bash
# Install SBT 1.9.6 (required)
curl -L https://github.com/sbt/sbt/releases/download/v1.9.6/sbt-1.9.6.tgz | sudo tar -xz -C /opt/
export PATH="/opt/sbt/bin:$PATH"
echo 'export PATH="/opt/sbt/bin:$PATH"' >> ~/.bashrc
```

**Essential Build Commands (NEVER CANCEL - Set long timeouts):**
```bash
# Start PostgreSQL database (required for testing and running)
docker compose up -d

# Clean and compile - takes 3 minutes. NEVER CANCEL. Set timeout to 10+ minutes.
sbt clean compile

# Run all tests - takes 44 seconds. NEVER CANCEL. Set timeout to 5+ minutes.
sbt test

# Run unit tests only - takes 4 seconds
sbt "testOnly com.finance.unit.*"

# Run integration tests only - takes 15 seconds (requires Docker database)
sbt "testOnly com.finance.integration.*"
```

**Mandatory Code Quality Checks:**
```bash
# Check code formatting - takes 20 seconds. MANDATORY before commits.
sbt scalafmtCheckAll

# Format all code (run after making changes)
sbt scalafmtAll

# Generate API code from Smithy definitions - takes 3 seconds
sbt smithy4sCodegen
```

### Run the Application

**Start the API Server:**
```bash
# Ensure database is running first
docker compose up -d

# Start application - takes 15 seconds to start. NEVER CANCEL. Set timeout to 2+ minutes.
sbt run
# Application starts at http://localhost:9000
# Visit http://localhost:9000/health for health check
```

**Test Application Endpoints:**
```bash
# Health check
curl http://localhost:9000/health

# Build information  
curl http://localhost:9000/build-info

# List accounts
curl http://localhost:9000/accounts

# Create new account
curl -X POST http://localhost:9000/accounts \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Account", "accountType": "CHECKING", "initialBalance": {"amount": 1000.0, "currency": "USD"}}'

# List transactions
curl http://localhost:9000/transactions
```

### Database Management

**Database Operations:**
```bash
# Reset development database (use docker compose, NOT docker-compose)
docker rm -f -v finance-db
docker compose up -d

# Check database container status
docker compose ps

# Connect to database for debugging
psql -h localhost -p 5434 -U develop -d finance
# Password: Finance*25
```

**Database Configuration:**
- Host: localhost:5434
- Database: finance  
- User: develop
- Password: Finance*25 (from .env file)
- Migrations run automatically via Flyway on application startup

## Validation Scenarios

**ALWAYS manually validate changes by running complete end-to-end scenarios after making code changes:**

1. **Basic Application Health:**
   ```bash
   # Start database and application
   docker compose up -d
   sbt run
   # Wait for "Finance API Server started at" message
   curl http://localhost:9000/health
   # Should return: {"health":{"status":"HEALTHY",...}}
   ```

2. **Account Management Workflow:**
   ```bash
   # Create account
   curl -X POST http://localhost:9000/accounts \
     -H "Content-Type: application/json" \
     -d '{"name": "Test Account", "accountType": "CHECKING", "initialBalance": {"amount": 1000.0, "currency": "USD"}}'
   
   # List accounts (should include new account)
   curl http://localhost:9000/accounts
   
   # Update account (use returned ID from creation)
   curl -X PUT http://localhost:9000/accounts/{account-id} \
     -H "Content-Type: application/json" \
     -d '{"name": "Updated Account Name"}'
   ```

3. **Transaction Workflow:**
   ```bash
   # Create transaction (requires existing account and concept IDs)
   curl -X POST http://localhost:9000/transactions \
     -H "Content-Type: application/json" \
     -d '{"accountId": "{account-id}", "conceptId": "550e8400-e29b-41d4-a716-446655440012", "description": "Test Transaction", "amount": {"amount": 100.0, "currency": "USD"}}'
   
   # List transactions
   curl http://localhost:9000/transactions
   ```

**Test Coverage Validation:**
- ALWAYS run `sbt test` after code changes (44 seconds, 45 tests)
- Integration tests automatically handle database setup and cleanup
- Unit tests focus on domain logic (Money, entities)

## Build Times and Timeouts

**CRITICAL TIMING INFORMATION:**
- **Initial SBT setup**: 3+ minutes (downloads dependencies) - Set timeout to 10+ minutes
- **sbt compile**: 3 minutes - **NEVER CANCEL** - Set timeout to 10+ minutes  
- **sbt test**: 44 seconds (all 45 tests) - **NEVER CANCEL** - Set timeout to 5+ minutes
- **sbt scalafmtCheckAll**: 20 seconds - Set timeout to 2+ minutes
- **Application startup**: 15 seconds - Set timeout to 2+ minutes
- **Unit tests only**: 4 seconds
- **Integration tests only**: 15 seconds

**WARNING: NEVER CANCEL BUILDS OR TESTS.** SBT builds may appear to hang but are actually downloading dependencies or performing code generation. Always wait for completion.

## Project Structure Navigation

**Key Directories:**
```
src/main/scala/           # Main application code (244 source files)
├── account/              # Account domain (entities, services, repos)
├── transaction/          # Transaction management
├── transfer/             # Money transfers between accounts  
├── budget/               # Budget planning and tracking
├── concept/              # Income/expense categories
├── global/               # Health checks and system info
├── common/               # Shared domain types (Money, Currency, errors)
└── Main.scala            # Application entry point

src/main/smithy/          # API definitions (generates type-safe Scala code)
├── finance-api.smithy    # Main API service definition
├── account.smithy        # Account operations
├── transaction.smithy    # Transaction operations
└── ...

src/main/resources/
├── application.conf      # Configuration (database, server settings)
└── flyway/sql/          # Database migrations (V1__init.sql, etc.)

src/test/scala/
├── unit/                # Domain unit tests (4 tests)
└── integration/         # API integration tests (41 tests with TestContainers)
```

**Frequently Modified Files:**
- `src/main/scala/common/domain/Money.scala` - Money value object
- `src/main/resources/application.conf` - Configuration
- API definitions in `src/main/smithy/` - Regenerate with `sbt smithy4sCodegen`

## Configuration

**Environment Variables (override application.conf):**
- `SERVER_HOST`, `SERVER_PORT` - Server binding
- `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD` - Database connection

**Default Configuration:**
- Server: http://0.0.0.0:9000  
- Database: postgresql://localhost:5434/finance
- Test isolation: Each test gets fresh database via TestContainers

## Known Issues and Workarounds

**Commands that DON'T work:**
- `sbt flywayInfo` - Flyway SBT plugin not configured (use application startup for migrations)
- `./recreate-db` script uses `docker-compose` instead of `docker compose` (use manual commands)
- `/docs` endpoint returns 404 (Swagger documentation may be misconfigured)

**Working Alternatives:**
- Database reset: Use `docker rm -f -v finance-db && docker compose up -d`
- Flyway migrations: Run automatically on application startup
- API documentation: Use curl commands or inspect Smithy files

## CI/CD Integration

**GitHub Actions Workflow (.github/workflows/ci.yml):**
- Linting: `sbt scalafmtCheckAll` (10 minute timeout)
- Testing: `sbt test` (25 minute timeout)  
- Uses JDK 21 for CI builds
- TestContainers with PostgreSQL

**Pre-commit Validation:**
```bash
# MANDATORY before committing
sbt scalafmtAll
sbt test
```

## Common Tasks Reference

**Quick Commands:**
```bash
# ls -la (repository root)
build.sbt  docker-compose.yml  project/  recreate-db  src/  .env  README.md

# Essential file locations
src/main/resources/application.conf     # Configuration
src/main/smithy/finance-api.smithy     # Main API definition  
src/main/scala/Main.scala              # Application entry
project/Dependencies.scala             # Dependency management
```

**Domain Model Overview:**
- **Account**: Financial accounts (Checking, Savings, Credit, Investment)
- **Transaction**: Money movements linked to accounts and concepts
- **Transfer**: Money transfers between accounts
- **Concept**: Income/expense categories with hierarchical support
- **Budget**: Budget planning with spending analysis
- **Money**: Value object with currency (prevents calculation errors)

Always run end-to-end validation scenarios after making changes to ensure the Finance API functions correctly as a complete system.

## Scala 3 & Functional Programming Modernization

**This project follows modern Scala 3 and Functional Programming best practices. Leverage advanced language features for type safety, performance, and maintainability.**

### Key Modernization Areas

**Current Modern Features in Use:**
- Scala 3.5.2 with latest syntax
- Enums instead of sealed traits (see `AccountType.scala`)
- Opaque types for type-safe IDs (`AccountId`, `UserId`, etc.)
- Given/using for dependency injection
- Cats Effect 3.5.2 for pure functional programming

**Planned Improvements (Use GitHub Issue Templates):**
1. **Enhanced Enum Usage** - Convert remaining sealed traits to modern enums
2. **Union Types** - Replace sealed trait error hierarchies with union types
3. **Inline Methods** - Zero-cost abstractions for performance-critical code
4. **Extension Methods** - Domain-specific fluent APIs (e.g., `money.isPositive`)
5. **Advanced Given/Using** - Enhanced dependency injection patterns
6. **Match Types** - Compile-time type validation and transformation
7. **Pattern Matching** - Leverage Scala 3 exhaustiveness and guards
8. **Transparent Inline** - Compile-time optimizations and constants
9. **Metaprogramming** - Code generation with macros for boilerplate elimination

### Development Best Practices

**Code Quality Standards:**
- Use modern Scala 3 syntax over legacy patterns
- Prefer immutable data structures and pure functions
- Leverage the type system for compile-time safety
- Write expressive, domain-driven code with extension methods
- Use inline/transparent inline for performance-critical paths

**Architecture Patterns:**
- Domain-driven design with rich domain models
- Functional error handling with `Either` and custom error types
- Repository pattern with generic interfaces
- Service layer with dependency injection via given/using
- Clean separation between domain, application, and infrastructure layers

**Performance Considerations:**
- Use opaque types for zero-cost type safety
- Leverage inline methods for hot paths
- Prefer immutable collections with structural sharing
- Use Cats Effect for efficient async/concurrent programming

## Advanced Scala & Development Resources

**Essential Context and Best Practices:**
- [Claude Context for Scala Projects](https://github.com/zilliztech/claude-context) - Advanced context management
- [Vibe Coding in Scala](https://www.reddit.com/r/scala/comments/1k2as61/on_vibe_coding/) - Modern Scala development patterns
- [Scala Metals with MCP](https://softwaremill.com/a-beginners-guide-to-using-scala-metals-with-its-model-context-protocol-server/) - Enhanced IDE integration

**GitHub Copilot Best Practices:**
- [Awesome Copilot Instructions](https://github.com/Code-and-Sorts/awesome-copilot-instructions) - Comprehensive guidance
- [GitHub Awesome Copilot](https://github.com/github/awesome-copilot) - Official resources and patterns

**Clean Code & Functional Programming:**
- Follow functional programming principles with Cats Effect
- Use type-safe error handling with domain-specific error types
- Implement pure functions and referential transparency
- Leverage Scala 3's advanced type system features
- Write self-documenting code with expressive domain types

**When Contributing:**
- Check existing GitHub issue templates for systematic improvements
- Use modern Scala 3 features over legacy approaches  
- Ensure type safety and compile-time validation
- Follow the established domain modeling patterns
- Maintain backward compatibility while modernizing incrementally

**Testing Strategy:**
- Write property-based tests for domain logic
- Use TestContainers for integration testing
- Test both happy path and error scenarios thoroughly
- Validate performance characteristics of optimizations
- Ensure exhaustive pattern matching in tests
