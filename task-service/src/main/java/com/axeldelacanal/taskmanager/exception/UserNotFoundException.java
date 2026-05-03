package com.axeldelacanal.taskmanager.exception;

/**
 * Thrown when the referenced user does not exist in user-service.
 * Triggers a 404 response via {@link UserNotFoundExceptionMapper}.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(Long userId) {
        super("User with id " + userId + " not found");
    }
}
