package com.axeldelacanal.usermanager.resource;

import com.axeldelacanal.usermanager.dto.HealthResponse;
import com.axeldelacanal.usermanager.service.UserService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
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
}
