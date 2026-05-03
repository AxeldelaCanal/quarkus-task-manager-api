-- Sample data loaded automatically on drop-and-create (dev profile only)
INSERT INTO tasks (title, description, status, created_at, updated_at)
VALUES ('Setup Quarkus project', 'Initialize the project structure and dependencies', 'DONE', NOW(), NOW());

INSERT INTO tasks (title, description, status, created_at, updated_at)
VALUES ('Implement CRUD endpoints', 'Build all REST endpoints for task management', 'IN_PROGRESS', NOW(), NOW());

INSERT INTO tasks (title, description, status, created_at, updated_at)
VALUES ('Write unit tests', 'Add JUnit 5 + Mockito tests for the service layer', 'PENDING', NOW(), NOW());

INSERT INTO tasks (title, description, status, created_at, updated_at)
VALUES ('Configure Docker', 'Write Dockerfile and docker-compose for local dev', 'PENDING', NOW(), NOW());
