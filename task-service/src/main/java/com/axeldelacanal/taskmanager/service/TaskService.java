package com.axeldelacanal.taskmanager.service;

import com.axeldelacanal.taskmanager.client.UserServiceClient;
import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.PageResponse;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.exception.UserNotFoundException;
import com.axeldelacanal.taskmanager.exception.UserServiceUnavailableException;
import com.axeldelacanal.taskmanager.repository.TaskRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class TaskService {

    private static final Logger LOG = Logger.getLogger(TaskService.class);

    @Inject
    TaskRepository taskRepository;

    @Inject
    @RestClient
    UserServiceClient userServiceClient;

    public PageResponse<TaskResponse> findAll(TaskStatus status, int page, int size) {
        List<Task> tasks;
        long total;
        if (status != null) {
            tasks = taskRepository.findByStatusPaged(status, page, size);
            total = taskRepository.countByStatus(status);
        } else {
            tasks = taskRepository.findAllPaged(page, size);
            total = taskRepository.count();
        }
        List<TaskResponse> content = tasks.stream().map(TaskResponse::from).toList();
        return PageResponse.of(content, page, size, total);
    }

    public TaskResponse findById(Long id) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        validateUserExists(request.userId);

        Task task = new Task();
        task.setUserId(request.userId);
        task.setTitle(request.title);
        task.setDescription(request.description);
        task.setStatus(request.status != null ? request.status : TaskStatus.PENDING);
        taskRepository.persist(task);

        LOG.infof("Task created: id=%d title='%s' userId=%d", task.getId(), task.getTitle(), task.getUserId());
        return TaskResponse.from(task);
    }

    @Transactional
    public TaskResponse update(Long id, TaskRequest request) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(request.title);
        if (request.description != null) {
            task.setDescription(request.description);
        }
        if (request.status != null) {
            task.setStatus(request.status);
        }

        LOG.infof("Task updated: id=%d title='%s' status=%s", task.getId(), task.getTitle(), task.getStatus());
        return TaskResponse.from(task);
    }

    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findByIdOptional(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        taskRepository.delete(task);
        LOG.infof("Task deleted: id=%d", id);
    }

    private void validateUserExists(Long userId) {
        try (Response response = userServiceClient.findById(userId)) {
            int status = response.getStatus();
            if (status == Response.Status.NOT_FOUND.getStatusCode()) {
                LOG.warnf("User not found in user-service: userId=%d", userId);
                throw new UserNotFoundException(userId);
            }
            if (status >= 500) {
                LOG.errorf("user-service returned %d for userId=%d", status, userId);
                throw new UserServiceUnavailableException(
                        "user-service returned an unexpected error (HTTP " + status + ")");
            }
        } catch (ProcessingException e) {
            LOG.errorf(e, "Cannot reach user-service for userId=%d", userId);
            throw new UserServiceUnavailableException("user-service is unreachable", e);
        }
    }
}
