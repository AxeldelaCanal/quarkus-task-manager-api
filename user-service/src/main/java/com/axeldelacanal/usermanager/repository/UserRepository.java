package com.axeldelacanal.usermanager.repository;

import com.axeldelacanal.usermanager.domain.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repositorio Panache para la entidad {@link User}.
 */
@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}
