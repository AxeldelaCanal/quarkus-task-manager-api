package com.axeldelacanal.usermanager.service;

import com.axeldelacanal.usermanager.domain.User;
import com.axeldelacanal.usermanager.dto.LoginRequest;
import com.axeldelacanal.usermanager.dto.LoginResponse;
import com.axeldelacanal.usermanager.dto.UserRequest;
import com.axeldelacanal.usermanager.dto.UserResponse;
import com.axeldelacanal.usermanager.exception.InvalidCredentialsException;
import com.axeldelacanal.usermanager.exception.UserAlreadyExistsException;
import com.axeldelacanal.usermanager.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.time.Duration;

@ApplicationScoped
public class UserService {

    private static final Logger LOG = Logger.getLogger(UserService.class);

    @Inject
    UserRepository userRepository;

    public boolean isHealthy() {
        return userRepository != null;
    }

    public boolean existsById(Long id) {
        return userRepository.findByIdOptional(id).isPresent();
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            LOG.warnf("Username already taken: %s", request.username);
            throw new UserAlreadyExistsException("Username already taken: " + request.username);
        }
        if (userRepository.findByEmail(request.email).isPresent()) {
            LOG.warnf("Email already registered: %s", request.email);
            throw new UserAlreadyExistsException("Email already registered: " + request.email);
        }

        User user = new User();
        user.setUsername(request.username);
        user.setEmail(request.email);
        user.setPassword(BcryptUtil.bcryptHash(request.password));

        userRepository.persist(user);
        LOG.infof("User created: id=%d username='%s'", user.getId(), user.getUsername());
        return UserResponse.from(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username)
                .orElseThrow(InvalidCredentialsException::new);

        if (!BcryptUtil.matches(request.password, user.getPassword())) {
            LOG.warnf("Failed login attempt for username='%s'", request.username);
            throw new InvalidCredentialsException();
        }

        String token = Jwt.issuer("task-manager-api")
                .subject(user.getUsername())
                .groups("user")
                .claim("userId", user.getId())
                .expiresIn(Duration.ofHours(1))
                .sign();

        LOG.infof("User logged in: username='%s'", user.getUsername());
        return new LoginResponse(token, 3600);
    }
}
