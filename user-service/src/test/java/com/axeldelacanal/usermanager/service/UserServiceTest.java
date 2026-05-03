package com.axeldelacanal.usermanager.service;

import com.axeldelacanal.usermanager.domain.User;
import com.axeldelacanal.usermanager.dto.UserRequest;
import com.axeldelacanal.usermanager.dto.UserResponse;
import com.axeldelacanal.usermanager.dto.LoginRequest;
import com.axeldelacanal.usermanager.exception.InvalidCredentialsException;
import com.axeldelacanal.usermanager.exception.UserAlreadyExistsException;
import com.axeldelacanal.usermanager.repository.UserRepository;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private static final String PLAIN_PASSWORD = "secret123";
    private String hashedPassword;
    private User existingUser;

    @BeforeEach
    void setUp() {
        hashedPassword = BcryptUtil.bcryptHash(PLAIN_PASSWORD);
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("axel");
        existingUser.setEmail("axel@example.com");
        existingUser.setPassword(hashedPassword);
    }

    // =========================================================================
    // isHealthy
    // =========================================================================

    @Test
    @DisplayName("isHealthy returns true when repository is injected")
    void isHealthy_returnsTrue() {
        assertTrue(userService.isHealthy());
    }

    // =========================================================================
    // existsById
    // =========================================================================

    @Test
    @DisplayName("existsById returns true when user exists")
    void existsById_existingUser_returnsTrue() {
        when(userRepository.findByIdOptional(1L)).thenReturn(Optional.of(existingUser));

        assertTrue(userService.existsById(1L));
    }

    @Test
    @DisplayName("existsById returns false when user does not exist")
    void existsById_missingUser_returnsFalse() {
        when(userRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        assertFalse(userService.existsById(99L));
    }

    // =========================================================================
    // create
    // =========================================================================

    @Test
    @DisplayName("create persists user with BCrypt-hashed password")
    void create_newUser_storesHashedPassword() {
        UserRequest request = new UserRequest();
        request.username = "newuser";
        request.email = "new@example.com";
        request.password = "plaintext";

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        doNothing().when(userRepository).persist(captor.capture());

        userService.create(request);

        User persisted = captor.getValue();
        assertNotEquals("plaintext", persisted.getPassword());
        assertTrue(BcryptUtil.matches("plaintext", persisted.getPassword()));
    }

    @Test
    @DisplayName("create returns response without exposing password")
    void create_newUser_responseHasNoPassword() {
        UserRequest request = new UserRequest();
        request.username = "newuser";
        request.email = "new@example.com";
        request.password = "plaintext";

        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        doNothing().when(userRepository).persist(any(User.class));

        UserResponse response = userService.create(request);

        assertEquals("newuser", response.username);
        assertEquals("new@example.com", response.email);
    }

    @Test
    @DisplayName("create throws UserAlreadyExistsException on duplicate username")
    void create_duplicateUsername_throwsException() {
        UserRequest request = new UserRequest();
        request.username = "axel";
        request.email = "other@example.com";
        request.password = "secret";

        when(userRepository.findByUsername("axel")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(request));
        verify(userRepository, never()).persist(any(User.class));
    }

    @Test
    @DisplayName("create throws UserAlreadyExistsException on duplicate email")
    void create_duplicateEmail_throwsException() {
        UserRequest request = new UserRequest();
        request.username = "other";
        request.email = "axel@example.com";
        request.password = "secret";

        when(userRepository.findByUsername("other")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("axel@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(UserAlreadyExistsException.class, () -> userService.create(request));
        verify(userRepository, never()).persist(any(User.class));
    }

    // =========================================================================
    // login
    // =========================================================================

    @Test
    @DisplayName("login throws InvalidCredentialsException when username not found")
    void login_unknownUsername_throwsException() {
        LoginRequest request = new LoginRequest();
        request.username = "ghost";
        request.password = PLAIN_PASSWORD;

        when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }

    @Test
    @DisplayName("login throws InvalidCredentialsException when password is wrong")
    void login_wrongPassword_throwsException() {
        LoginRequest request = new LoginRequest();
        request.username = "axel";
        request.password = "wrongpassword";

        when(userRepository.findByUsername("axel")).thenReturn(Optional.of(existingUser));

        assertThrows(InvalidCredentialsException.class, () -> userService.login(request));
    }
}
