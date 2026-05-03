package com.axeldelacanal.taskmanager.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client for user-service.
 * Validates user existence before task creation.
 * URL configured via {@code quarkus.rest-client.user-service.url} in application.properties.
 */
@Path("/users")
@RegisterRestClient(configKey = "user-service")
public interface UserServiceClient {

    /**
     * Calls {@code GET /users/{id}} on user-service.
     *
     * @param id user identifier
     * @return 200 if the user exists, 404 otherwise
     */
    @GET
    @Path("/{id}")
    Response findById(@PathParam("id") Long id);
}
