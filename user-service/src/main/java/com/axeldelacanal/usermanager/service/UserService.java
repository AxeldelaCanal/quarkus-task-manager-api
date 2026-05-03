package com.axeldelacanal.usermanager.service;

import com.axeldelacanal.usermanager.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Lógica de negocio relacionada con usuarios.
 */
@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;

    /**
     * Comprueba que la capa de servicio está operativa (usado por el health check HTTP).
     */
    public boolean isHealthy() {
        return userRepository != null;
    }
}
