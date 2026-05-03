package com.axeldelacanal.taskmanager.service;

import com.axeldelacanal.taskmanager.client.UserServiceClient;
import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.exception.UserNotFoundException;
import com.axeldelacanal.taskmanager.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Application service layer for task management.
 * Owns all business logic and transaction boundaries; delegates persistence to the repository.
 */
@ApplicationScoped
public class TaskService {

    @Inject
    TaskRepository taskRepository;

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    /**
     * Returns all tasks, optionally filtered by status.
     *
     * @param status optional filter; if null, all tasks are returned
     */
    public List<TaskResponse> findAll(TaskStatus status) {
        List<Task> tasks = (status != null)
                ? taskRepository.findByStatus(status)
                : taskRepository.listAll();
        return tasks.stream().map(TaskResponse::from).collect(Collectors.toList());
    }

    /**
     * Returns the task with the given ID.
     *
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }

    /**
     * Persists a new task. Validates that the referenced user exists in user-service
     * before creating the task. Status defaults to PENDING if not provided.
     *
     * @throws UserNotFoundException if user-service responds with 404 for the given userId
     */
    @Transactional
    public TaskResponse create(TaskRequest request) {
        validateUserExists(request.userId);

        Task task = new Task();
        task.setUserId(request.userId);
        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setStatus(request.status != null ? request.status : TaskStatus.PENDING);
        taskRepository.persist(task);
        return TaskResponse.from(task);
    }

    /**
     * Updates an existing task's fields. Only non-null status values in the request
     * trigger a status change.
     *
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(request.title);
        task.setDescription(request.description);
        if (request.status != null) {
            task.setStatus(request.status);
        }

        return TaskResponse.from(task);
    }

    /**
     * Deletes the task with the given ID.
     *
     * @throws TaskNotFoundException if no task exists with the given ID
     */
    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
    }

    /**
     * Calls user-service to verify the user exists.
     * Throws {@link UserNotFoundException} if user-service returns 404.
     */
    private void validateUserExists(Long userId) {
        try (Response response = userServiceClient.findById(userId)) {
            if (response.getStatus() == Response.Status.NOT_FOUND.getStatusCode()) {
                throw new UserNotFoundException(userId);
            }
        }
    }
}
