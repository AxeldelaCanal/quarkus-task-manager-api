package com.axeldelacanal.taskmanager.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

/**
 * Maps Bean Validation failures to HTTP 400 with all violation messages joined.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(new ErrorResponse(400, "Bad Request", message))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
