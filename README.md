# Quarkus Task Manager API

> A production-ready REST API for task management built with Quarkus, JPA/Panache, and PostgreSQL.

---

## 🇦🇷 Español

### Descripción

API REST multi-módulo para gestión de tareas construida con **Quarkus 3.24** y Java 17. Implementa una arquitectura de microservicios, comunicación inter-servicios vía MicroProfile REST Client, acceso a datos con Hibernate ORM + Panache, y orquestación local con Docker Compose.

### Arquitectura

El sistema está compuesto por dos microservicios independientes, cada uno con su propia base de datos, comunicándose vía peticiones HTTP:

```text
+-----------------------+   Verifica   +-----------------------+
|     task-service      | ------------>|     user-service      |
|     (Puerto 8080)     |  usuario OK  |     (Puerto 8081)     |
+-----------------------+     REST     +-----------------------+
           |                                  |
           v                                  v
+-----------------------+          +-----------------------+
|    postgres-tasks     |          |    postgres-users     |
|     (Puerto 5432)     |          |     (Puerto 5433)     |
+-----------------------+          +-----------------------+
```

### Stack Tecnológico

| Tecnología | Versión | Rol |
|---|---|---|
| Quarkus | 3.24.1 | Framework backend |
| Java | 17 | Lenguaje |
| Quarkus REST | — | Capa REST (JAX-RS) |
| MicroProfile REST Client | — | Comunicación inter-servicios |
| Hibernate ORM + Panache | — | ORM / acceso a datos |
| PostgreSQL | 16 | Base de datos |
| SmallRye OpenAPI | — | Documentación Swagger |
| JUnit 5 + Mockito | 5.x | Testing |
| Docker & Compose | — | Containerización |

### Cómo correr localmente

#### Orquestación completa con Docker Compose (recomendado)

Levanta ambos microservicios y sus respectivas bases de datos (4 contenedores):

```bash
docker-compose up --build
```

- **Task Service API:** `http://localhost:8080`
- **User Service API:** `http://localhost:8081`
- **Swagger UI (Tasks):** `http://localhost:8080/q/swagger-ui`
- **Swagger UI (Users):** `http://localhost:8081/q/swagger-ui`

### Endpoints

#### Task Service (Puerto 8080)

| Método | Ruta | Descripción | Código exitoso |
|---|---|---|---|
| `GET` | `/tasks` | Listar todas las tareas | `200 OK` |
| `GET` | `/tasks?status=PENDING` | Filtrar por estado | `200 OK` |
| `GET` | `/tasks/{id}` | Obtener tarea por ID | `200 OK` |
| `POST` | `/tasks` | Crear nueva tarea | `201 Created` |
| `PUT` | `/tasks/{id}` | Actualizar tarea | `200 OK` |
| `DELETE` | `/tasks/{id}` | Eliminar tarea | `204 No Content` |

#### User Service (Puerto 8081)

| Método | Ruta | Descripción | Código exitoso |
|---|---|---|---|
| `GET` | `/users/health` | Health check del servicio | `200 OK` |
| `GET` | `/users/{id}` | Verificar si existe un usuario | `200 OK` / `404 Not Found` |

#### Estados disponibles

- `PENDING` — Pendiente
- `IN_PROGRESS` — En progreso
- `DONE` — Completada

#### Ejemplos de requests

**Crear tarea:**
```bash
curl -X POST http://localhost:8080/tasks \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "Setup CI/CD", "description": "Configure GitHub Actions pipeline", "status": "PENDING"}'
```

**Actualizar estado:**
```bash
curl -X PUT http://localhost:8080/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "title": "Setup CI/CD", "status": "DONE"}'
```

**Filtrar por estado:**
```bash
curl http://localhost:8080/tasks?status=IN_PROGRESS
```

### Códigos de error

| Código | Situación |
|---|---|
| `400 Bad Request` | Validación fallida (título vacío, usuario faltante) |
| `404 Not Found` | Tarea o usuario no encontrado por ID |
| `500 Internal Server Error` | Error inesperado del servidor |

### Tests

```bash
./mvnw test
```

11 tests unitarios sobre la capa de servicio — sin base de datos, usando Mockito puro y verificando interacciones con el REST Client.

---

## 🇬🇧 English

### Description

A multi-module REST API for task management built with **Quarkus 3.24** and Java 17. Implements a microservices architecture, inter-service communication via MicroProfile REST Client, data access with Hibernate ORM + Panache, and local orchestration with Docker Compose.

### Architecture

The system consists of two independent microservices, each with its own database, communicating via HTTP requests:

```text
+-----------------------+    Verifies  +-----------------------+
|     task-service      | ------------>|     user-service      |
|     (Port 8080)       |   user OK    |     (Port 8081)       |
+-----------------------+      REST    +-----------------------+
           |                                  |
           v                                  v
+-----------------------+          +-----------------------+
|    postgres-tasks     |          |    postgres-users     |
|     (Port 5432)       |          |     (Port 5433)       |
+-----------------------+          +-----------------------+
```

### Tech Stack

| Technology | Version | Role |
|---|---|---|
| Quarkus | 3.24.1 | Backend framework |
| Java | 17 | Language |
| Quarkus REST | — | REST layer (JAX-RS) |
| MicroProfile REST Client | — | Inter-service communication |
| Hibernate ORM + Panache | — | ORM / data access |
| PostgreSQL | 16 | Database |
| SmallRye OpenAPI | — | Swagger documentation |
| JUnit 5 + Mockito | 5.x | Testing |
| Docker & Compose | — | Containerization |

### Running locally

#### Full orchestration with Docker Compose (recommended)

Spins up both microservices and their respective databases (4 containers):

```bash
docker-compose up --build
```

- **Task Service API:** `http://localhost:8080`
- **User Service API:** `http://localhost:8081`
- **Swagger UI (Tasks):** `http://localhost:8080/q/swagger-ui`
- **Swagger UI (Users):** `http://localhost:8081/q/swagger-ui`

### API Endpoints

#### Task Service (Port 8080)

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/tasks` | List all tasks | `200 OK` |
| `GET` | `/tasks?status=PENDING` | Filter by status | `200 OK` |
| `GET` | `/tasks/{id}` | Get task by ID | `200 OK` |
| `POST` | `/tasks` | Create new task | `201 Created` |
| `PUT` | `/tasks/{id}` | Update task | `200 OK` |
| `DELETE` | `/tasks/{id}` | Delete task | `204 No Content` |

#### User Service (Port 8081)

| Method | Path | Description | Success |
|---|---|---|---|
| `GET` | `/users/health` | Service health check | `200 OK` |
| `GET` | `/users/{id}` | Check if user exists | `200 OK` / `404 Not Found` |

#### Request body (POST / PUT)

```json
{
  "userId": 1,
  "title": "string (required, max 255)",
  "description": "string (optional, max 2000)",
  "status": "PENDING | IN_PROGRESS | DONE"
}
```

#### Response body

```json
{
  "id": 1,
  "userId": 1,
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

11 unit tests for the service layer — no database required, pure Mockito including REST Client mocking.

---

## Author

**Axel E. de la Canal** — [GitHub](https://github.com/axeldelacanal)
