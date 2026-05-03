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

    /**
     * Indica si existe un usuario con el ID dado.
     * Usado por task-service para validar el propietario de una tarea antes de crearla.
     *
     * @param id identificador del usuario
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean existsById(Long id) {
        return userRepository.findByIdOptional(id).isPresent();
    }
}
