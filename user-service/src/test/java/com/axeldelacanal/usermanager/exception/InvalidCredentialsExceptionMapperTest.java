package com.axeldelacanal.usermanager.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionMapperTest {

    private final InvalidCredentialsExceptionMapper mapper = new InvalidCredentialsExceptionMapper();

    @Test
    void mapsTo401() {
        Response response = mapper.toResponse(new InvalidCredentialsException());

        assertEquals(401, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(401, body.status);
        assertEquals("Invalid username or password", body.message);
    }
}
