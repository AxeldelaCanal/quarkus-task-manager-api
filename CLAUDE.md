# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Process
- Use SDD (explore, proposal, design, tasks) for every non-trivial change.
- Do not guess APIs, versions, or package names — verify against the BOM or local Maven cache first.
- Never build after making changes (per project rule).

## Commands

```bash
# Run all tests (both modules)
./mvnw test

# Run tests for a single module
./mvnw test -pl task-service
./mvnw test -pl user-service

# Run a single test class
./mvnw test -pl task-service -Dtest=TaskServiceTest

# Dev mode (requires a running PostgreSQL — see docker-compose.yml for credentials)
./mvnw quarkus:dev -pl task-service
./mvnw quarkus:dev -pl user-service

# Full stack via Docker Compose (builds images, spins up 5 containers)
docker-compose up --build
```

## Architecture

Maven multi-module project: one parent POM aggregates `task-service` and `user-service`. Each module is an independent Quarkus application with its own PostgreSQL database.

```
nginx (443/80)
  ├── /tasks  → task-service:8080
  └── /users  → user-service:8081
       task-service also calls user-service internally via MicroProfile REST Client
```

**nginx** terminates TLS. Self-signed cert lives in `nginx/certs/`. In production, replace with a CA-issued cert. The microservices use `expose:` (not `ports:`), so they are not reachable directly from the host when running via Compose.

**task-service** owns task CRUD. Before persisting a new task it calls `GET /users/{id}` on user-service to validate the owner. If user-service returns 5xx or is unreachable, it throws `UserServiceUnavailableException` → 503.

**user-service** owns user registration and authentication. It issues RS256 JWTs via `POST /users/login`. All task-service endpoints (except `/tasks/health`) require `Authorization: Bearer <token>` and are protected with `@RolesAllowed("user")`.

### JWT Key Pair
- Private key (signing): `user-service/src/main/resources/keys/privateKey.pem`
- Public key (verification): `task-service/src/main/resources/keys/publicKey.pem`
- Both keys must be kept in sync. To regenerate: `openssl genrsa -out privateKey.pem 2048 && openssl rsa -pubout -in privateKey.pem -out publicKey.pem`

### Layer Conventions (both modules)
```
domain/      JPA entities
dto/         Request/Response DTOs — factory method `Foo.from(Entity)` for mapping
exception/   RuntimeException subclasses + @Provider ExceptionMappers
repository/  PanacheRepository subclasses — add domain-specific queries here
service/     @ApplicationScoped, owns @Transactional boundaries, maps to DTOs
resource/    JAX-RS @Path classes — no business logic, delegates to service
client/      MicroProfile REST Client interfaces (task-service only)
```

### Pagination
`GET /tasks` returns `PageResponse<TaskResponse>` with `?page=0&size=20` (max 100). The `PageResponse` DTO carries `content`, `page`, `size`, `total`, `totalPages`.

### Testing
All tests are pure Mockito unit tests — no `@QuarkusTest`, no database required. `UserService.login()` happy path (JWT signing) cannot be unit-tested without the Quarkus config system; only failure paths are covered in unit tests.

To add a new exception: create the `RuntimeException` subclass, create the `@Provider ExceptionMapper`, add a unit test for the mapper.
