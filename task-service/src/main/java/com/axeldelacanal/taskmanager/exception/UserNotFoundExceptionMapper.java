package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link UserNotFoundException} to a 404 HTTP response with a structured error body.
 */
@Provider
public class UserNotFoundExceptionMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(404, "Not Found", exception.getMessage()))
                .build();
    }
}
