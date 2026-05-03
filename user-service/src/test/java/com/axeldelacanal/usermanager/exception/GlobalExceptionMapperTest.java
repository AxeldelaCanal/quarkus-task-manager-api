package com.axeldelacanal.usermanager.exception;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionMapperTest {

    private final GlobalExceptionMapper mapper = new GlobalExceptionMapper();

    @Test
    void passesThrough_webApplicationException() {
        Response original = Response.status(422).build();
        WebApplicationException wae = new WebApplicationException(original);

        Response response = mapper.toResponse(wae);

        assertEquals(422, response.getStatus());
    }

    @Test
    void wrapsUnknownException_as500() {
        Response response = mapper.toResponse(new RuntimeException("boom"));

        assertEquals(500, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(500, body.status);
    }
}
