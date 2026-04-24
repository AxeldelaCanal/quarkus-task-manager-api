# Quarkus Task Manager API

> A production-ready REST API for task management built with Quarkus, JPA/Panache, and PostgreSQL.

---

## 🇦🇷 Español

### Descripción

API REST para gestión de tareas construida con **Quarkus 3.10** y Java 17. Implementa arquitectura en capas (Resource → Service → Repository), validaciones con Bean Validation, manejo global de excepciones y documentación automática con Swagger UI.

### Stack Tecnológico

| Tecnología | Versión | Rol |
|---|---|---|
| Quarkus | 3.10.0 | Framework backend |
| Java | 17 | Lenguaje |
| RESTEasy Reactive | — | Capa REST (JAX-RS) |
| Hibernate ORM + Panache | — | ORM / acceso a datos |
| PostgreSQL | 16 | Base de datos |
| SmallRye OpenAPI | — | Documentación Swagger |
| JUnit 5 + Mockito | 5.x | Testing |
| Docker | — | Containerización |

### Cómo correr localmente

#### Opción 1 — Con Docker Compose (recomendado)

```bash
docker-compose up --build
```

La API queda disponible en `http://localhost:8080`
Swagger UI en `http://localhost:8080/q/swagger-ui`

#### Opción 2 — Dev mode (requiere PostgreSQL local)

1. Levantá solo la base de datos:
```bash
docker-compose up postgres
```

2. Corré la app en modo dev (hot reload):
```bash
./mvnw quarkus:dev
```

#### Variables de entorno (producción)

| Variable | Descripción |
|---|---|
| `DB_URL` | JDBC URL de la base de datos |
| `DB_USERNAME` | Usuario de PostgreSQL |
| `DB_PASSWORD` | Contraseña de PostgreSQL |

### Endpoints

#### Tasks

| Método | Ruta | Descripción | Código exitoso |
|---|---|---|---|
| `GET` | `/tasks` | Listar todas las tareas | `200 OK` |
| `GET` | `/tasks?status=PENDING` | Filtrar por estado | `200 OK` |
| `GET` | `/tasks/{id}` | Obtener tarea por ID | `200 OK` |
| `POST` | `/tasks` | Crear nueva tarea | `201 Created` |
| `PUT` | `/tasks/{id}` | Actualizar tarea | `200 OK` |
| `DELETE` | `/tasks/{id}` | Eliminar tarea | `204 No Content` |

#### Estados disponibles

- `PENDING` — Pendiente
- `IN_PROGRESS` — En progreso
- `DONE` — Completada

#### Ejemplos de requests

**Crear tarea:**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"title": "Setup CI/CD", "description": "Configure GitHub Actions pipeline", "status": "PENDING"}'
```

**Actualizar estado:**
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"title": "Setup CI/CD", "status": "DONE"}'
```

**Filtrar por estado:**
```bash
curl http://localhost:8080/tasks?status=IN_PROGRESS
```

### Códigos de error

| Código | Situación |
|---|---|
| `400 Bad Request` | Validación fallida (título vacío, campo inválido) |
| `404 Not Found` | Tarea no encontrada por ID |
| `500 Internal Server Error` | Error inesperado del servidor |

### Tests

```bash
./mvnw test
```

9 tests unitarios sobre la capa de servicio — sin base de datos, usando Mockito puro.

---

## 🇬🇧 English

### Description

A REST API for task management built with **Quarkus 3.10** and Java 17. Implements a layered architecture (Resource → Service → Repository), Bean Validation, global exception handling, and automatic Swagger UI documentation.

### Tech Stack

| Technology | Version | Role |
|---|---|---|
| Quarkus | 3.10.0 | Backend framework |
| Java | 17 | Language |
| RESTEasy Reactive | — | REST layer (JAX-RS) |
| Hibernate ORM + Panache | — | ORM / data access |
| PostgreSQL | 16 | Database |
| SmallRye OpenAPI | — | Swagger documentation |
| JUnit 5 + Mockito | 5.x | Testing |
| Docker | — | Containerization |

### Running locally

#### Option 1 — Docker Compose (recommended)

```bash
docker-compose up --build
```

API available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/q/swagger-ui`

#### Option 2 — Dev mode (requires local PostgreSQL)

1. Start only the database:
```bash
docker-compose up postgres
```

2. Run in dev mode (hot reload enabled):
```bash
./mvnw quarkus:dev
```

#### Environment variables (production)

| Variable | Description |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | PostgreSQL username |
| `DB_PASSWORD` | PostgreSQL password |

### API Endpoints

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/tasks` | List all tasks | `200 OK` |
| `GET` | `/tasks?status=PENDING` | Filter by status | `200 OK` |
| `GET` | `/tasks/{id}` | Get task by ID | `200 OK` |
| `POST` | `/tasks` | Create new task | `201 Created` |
| `PUT` | `/tasks/{id}` | Update task | `200 OK` |
| `DELETE` | `/tasks/{id}` | Delete task | `204 No Content` |

#### Request body (POST / PUT)

```json
{
  "title": "string (required, max 255)",
  "description": "string (optional, max 2000)",
  "status": "PENDING | IN_PROGRESS | DONE"
}
```

#### Response body

```json
{
  "id": 1,
  "title": "Setup CI/CD",
  "description": "Configure GitHub Actions pipeline",
  "status": "PENDING",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

### Project Structure

```
src/
└── main/
    └── java/com/axeldelacanal/taskmanager/
        ├── domain/          # JPA entities and enums
        ├── dto/             # Request/Response DTOs
        ├── exception/       # Custom exceptions and mappers
        ├── repository/      # Panache repositories
        ├── service/         # Business logic
        └── resource/        # JAX-RS endpoints
```

### Running tests

```bash
./mvnw test
```

9 unit tests for the service layer — no database required, pure Mockito.

---

## Author

**Axel E. de la Canal** — [GitHub](https://github.com/axeldelacanal)
