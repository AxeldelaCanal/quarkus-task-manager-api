package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceUnavailableExceptionMapperTest {

    private final UserServiceUnavailableExceptionMapper mapper = new UserServiceUnavailableExceptionMapper();

    @Test
    void mapsTo503WithMessage() {
        Response response = mapper.toResponse(new UserServiceUnavailableException("user-service is unreachable"));

        assertEquals(503, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(503, body.status);
        assertEquals("user-service is unreachable", body.message);
    }
}
