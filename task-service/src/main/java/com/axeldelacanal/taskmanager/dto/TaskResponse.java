package com.axeldelacanal.taskmanager.dto;

import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;

import java.time.LocalDateTime;

/**
 * Output DTO for task responses. Decouples the API contract from the domain entity.
 */
public class TaskResponse {

    public Long id;
    public Long userId;
    public String title;
    public String description;
    public TaskStatus status;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    /**
     * Factory method — maps a domain entity to a response DTO.
     */
    public static TaskResponse from(Task task) {
        TaskResponse response = new TaskResponse();
        response.id = task.getId();
        response.userId = task.getUserId();
        response.title = task.getTitle();
        response.description = task.getDescription();
        response.status = task.getStatus();
        response.createdAt = task.getCreatedAt();
        response.updatedAt = task.getUpdatedAt();
        return response;
    }
}
