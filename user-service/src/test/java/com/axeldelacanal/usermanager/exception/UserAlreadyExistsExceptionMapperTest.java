package com.axeldelacanal.usermanager.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAlreadyExistsExceptionMapperTest {

    private final UserAlreadyExistsExceptionMapper mapper = new UserAlreadyExistsExceptionMapper();

    @Test
    void mapsTo409WithMessage() {
        Response response = mapper.toResponse(new UserAlreadyExistsException("Username already taken: axel"));

        assertEquals(409, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(409, body.status);
        assertTrue(body.message.contains("axel"));
    }
}
