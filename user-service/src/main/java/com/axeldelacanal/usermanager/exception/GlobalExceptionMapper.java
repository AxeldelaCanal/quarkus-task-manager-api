package com.axeldelacanal.usermanager.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        if (exception instanceof WebApplicationException wae) {
            return wae.getResponse();
        }

        LOG.errorf(exception, "Unhandled exception: %s", exception.getMessage());
        return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(500, "Internal Server Error", "An unexpected error occurred"))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
