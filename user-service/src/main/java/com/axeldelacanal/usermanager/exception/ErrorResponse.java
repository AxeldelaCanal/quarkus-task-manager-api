package com.axeldelacanal.usermanager.exception;

import java.time.LocalDateTime;

/**
 * Respuesta de error estructurada para excepciones no previstas.
 */
public class ErrorResponse {

    public int status;
    public String error;
    public String message;
    public LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
