package com.axeldelacanal.taskmanager.exception;

/**
 * Thrown when a task with the given ID does not exist in the database.
 */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Task with id " + id + " not found");
    }
}
