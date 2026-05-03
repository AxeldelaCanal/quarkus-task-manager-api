package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNotFoundExceptionMapperTest {

    private final UserNotFoundExceptionMapper mapper = new UserNotFoundExceptionMapper();

    @Test
    void mapsTo404WithMessage() {
        Response response = mapper.toResponse(new UserNotFoundException(99L));

        assertEquals(404, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertTrue(body.message.contains("99"));
    }
}
