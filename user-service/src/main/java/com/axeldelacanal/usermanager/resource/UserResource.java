package com.axeldelacanal.usermanager.resource;

import com.axeldelacanal.usermanager.dto.HealthResponse;
import com.axeldelacanal.usermanager.dto.UserRequest;
import com.axeldelacanal.usermanager.dto.UserResponse;
import com.axeldelacanal.usermanager.service.UserService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management operations")
public class UserResource {

    @Inject
    UserService userService;

    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Returns 200 when the user service is up")
    @APIResponse(responseCode = "200", description = "Service healthy")
    public Response health() {
        if (userService.isHealthy()) {
            return Response.ok(new HealthResponse("UP")).build();
        }
        return Response.serverError().build();
    }

    @POST
    @Operation(summary = "Create user", description = "Registers a new user with a BCrypt-hashed password")
    @APIResponse(responseCode = "201", description = "User created")
    @APIResponse(responseCode = "400", description = "Validation error")
    @APIResponse(responseCode = "409", description = "Username or email already exists")
    public Response create(@Valid UserRequest request) {
        UserResponse created = userService.create(request);
        return Response.created(UriBuilder.fromResource(UserResource.class)
                        .path(String.valueOf(created.id))
                        .build())
                .entity(created)
                .build();
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Check user existence", description = "Returns 200 if the user exists, 404 otherwise")
    @APIResponse(responseCode = "200", description = "User exists")
    @APIResponse(responseCode = "404", description = "User not found")
    public Response getById(@PathParam("id") Long id) {
        if (userService.existsById(id)) {
            return Response.ok().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
