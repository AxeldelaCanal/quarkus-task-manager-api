package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class UserServiceUnavailableExceptionMapper implements ExceptionMapper<UserServiceUnavailableException> {

    @Override
    public Response toResponse(UserServiceUnavailableException exception) {
        return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                .type(MediaType.APPLICATION_JSON)
                .entity(new ErrorResponse(503, "Service Unavailable", exception.getMessage()))
                .build();
    }
}
