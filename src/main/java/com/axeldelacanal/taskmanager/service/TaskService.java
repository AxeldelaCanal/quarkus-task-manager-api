package com.axeldelacanal.taskmanager.service;

import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

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
     * Persists a new task. Status defaults to PENDING if not provided.
     */
    @Transactional
    public TaskResponse create(TaskRequest request) {
        Task task = new Task();
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
}
