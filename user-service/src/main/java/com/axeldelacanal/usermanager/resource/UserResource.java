package com.axeldelacanal.usermanager.resource;

import com.axeldelacanal.usermanager.dto.HealthResponse;
import com.axeldelacanal.usermanager.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

/**
 * Endpoints REST del dominio de usuarios.
 */
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
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

    /**
     * Verifica si existe un usuario con el ID dado.
     * Devuelve 200 con el ID si existe, 404 si no.
     * Consumido por task-service antes de crear una tarea.
     */
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
