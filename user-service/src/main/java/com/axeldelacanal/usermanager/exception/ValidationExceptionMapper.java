package com.axeldelacanal.usermanager.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

/**
 * Mapea fallos de Bean Validation a HTTP 400 con los mensajes unidos.
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
