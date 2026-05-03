package com.axeldelacanal.taskmanager.dto;

import com.axeldelacanal.taskmanager.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Input DTO for creating and updating tasks.
 * All validation constraints are enforced at the resource layer.
 */
public class TaskRequest {

    @NotNull(message = "userId is required")
    public Long userId;

    @NotBlank(message = "Title is required and must not be blank")
    @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    public String title;

    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    public String description;

    public TaskStatus status;
}
