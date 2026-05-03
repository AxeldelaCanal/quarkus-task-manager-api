package com.axeldelacanal.taskmanager.exception;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskNotFoundExceptionMapperTest {

    private final TaskNotFoundExceptionMapper mapper = new TaskNotFoundExceptionMapper();

    @Test
    void mapsTo404WithMessage() {
        Response response = mapper.toResponse(new TaskNotFoundException(42L));

        assertEquals(404, response.getStatus());
        ErrorResponse body = (ErrorResponse) response.getEntity();
        assertEquals(404, body.status);
        assertTrue(body.message.contains("42"));
    }
}
