package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

/**
 * Maps {@link TaskNotFoundException} to HTTP 404 with a structured JSON body.
 */
@Provider
public class TaskNotFoundExceptionMapper implements ExceptionMapper<TaskNotFoundException> {

    @Override
    public Response toResponse(TaskNotFoundException exception) {
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse(404, "Not Found", exception.getMessage()))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
