package com.axeldelacanal.usermanager.resource;

import com.axeldelacanal.usermanager.dto.*;
import com.axeldelacanal.usermanager.exception.InvalidCredentialsException;
import com.axeldelacanal.usermanager.exception.UserAlreadyExistsException;
import com.axeldelacanal.usermanager.service.UserService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResourceTest {

    @Mock
    UserService userService;

    @InjectMocks
    UserResource resource;

    @Test
    @DisplayName("health returns 200 with status UP when service is healthy")
    void health_healthy_returns200() {
        when(userService.isHealthy()).thenReturn(true);

        Response response = resource.health();

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("health returns 500 when service is not healthy")
    void health_unhealthy_returns500() {
        when(userService.isHealthy()).thenReturn(false);

        Response response = resource.health();

        assertEquals(500, response.getStatus());
    }

    @Test
    @DisplayName("create returns 201 with Location header and user body")
    void create_validRequest_returns201() {
        UserRequest request = new UserRequest();
        request.username = "axel";
        request.email = "axel@example.com";
        request.password = "secret123";

        UserResponse created = new UserResponse();
        created.id = 1L;
        created.username = "axel";
        created.email = "axel@example.com";
        when(userService.create(request)).thenReturn(created);

        Response response = resource.create(request);

        assertEquals(201, response.getStatus());
        assertNotNull(response.getLocation());
        UserResponse body = (UserResponse) response.getEntity();
        assertEquals("axel", body.username);
    }

    @Test
    @DisplayName("create propagates UserAlreadyExistsException on duplicate")
    void create_duplicateUsername_propagatesException() {
        UserRequest request = new UserRequest();
        request.username = "axel";
        request.email = "axel@example.com";
        request.password = "secret123";
        when(userService.create(any())).thenThrow(new UserAlreadyExistsException("Username already taken: axel"));

        assertThrows(UserAlreadyExistsException.class, () -> resource.create(request));
    }

    @Test
    @DisplayName("login returns 200 with token")
    void login_validCredentials_returns200() {
        LoginRequest request = new LoginRequest();
        request.username = "axel";
        request.password = "secret123";

        LoginResponse loginResponse = new LoginResponse("jwt-token", 3600);
        when(userService.login(request)).thenReturn(loginResponse);

        Response response = resource.login(request);

        assertEquals(200, response.getStatus());
        LoginResponse body = (LoginResponse) response.getEntity();
        assertEquals("jwt-token", body.token);
        assertEquals(3600, body.expiresIn);
    }

    @Test
    @DisplayName("login propagates InvalidCredentialsException on bad password")
    void login_badCredentials_propagatesException() {
        LoginRequest request = new LoginRequest();
        request.username = "axel";
        request.password = "wrong";
        when(userService.login(request)).thenThrow(new InvalidCredentialsException());

        assertThrows(InvalidCredentialsException.class, () -> resource.login(request));
    }

    @Test
    @DisplayName("getById returns 200 when user exists")
    void getById_existingUser_returns200() {
        when(userService.existsById(1L)).thenReturn(true);

        Response response = resource.getById(1L);

        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("getById returns 404 when user not found")
    void getById_missingUser_returns404() {
        when(userService.existsById(99L)).thenReturn(false);

        Response response = resource.getById(99L);

        assertEquals(404, response.getStatus());
    }
}
