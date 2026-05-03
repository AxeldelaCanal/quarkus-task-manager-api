package com.axeldelacanal.usermanager.dto;

/**
 * Cuerpo JSON del endpoint de salud del servicio de usuarios.
 */
public class HealthResponse {

    public String status;

    public HealthResponse(String status) {
        this.status = status;
    }
}
