package com.axeldelacanal.taskmanager.resource;

import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.HealthResponse;
import com.axeldelacanal.taskmanager.dto.PageResponse;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.service.TaskService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;


/**
 * JAX-RS resource exposing the Task Manager REST API.
 * Delegates all business logic to {@link TaskService}.
 */
@ApplicationScoped
@Path("/tasks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Tasks", description = "Task lifecycle management")
public class TaskResource {

    @Inject
    TaskService taskService;

    @GET
    @Path("/health")
    @Operation(summary = "Health check", description = "Returns 200 when the task service is up")
    @APIResponse(responseCode = "200", description = "Service healthy")
    public Response health() {
        return Response.ok(new HealthResponse("UP")).build();
    }

    @GET
    @Operation(summary = "List all tasks", description = "Returns a paginated list of tasks, optionally filtered by status")
    @APIResponse(responseCode = "200", description = "Paginated list of tasks",
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = PageResponse.class)))
    public PageResponse<TaskResponse> getAll(
            @Parameter(description = "Filter by task status: PENDING, IN_PROGRESS, DONE")
            @QueryParam("status") TaskStatus status,
            @Parameter(description = "Zero-based page index (default 0)")
            @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size (default 20, max 100)")
            @QueryParam("size") @DefaultValue("20") int size) {
        int clampedSize = Math.min(size, 100);
        return taskService.findAll(status, page, clampedSize);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get task by ID")
    @APIResponse(responseCode = "200", description = "Task found")
    @APIResponse(responseCode = "404", description = "Task not found")
    public TaskResponse getById(@PathParam("id") Long id) {
        return taskService.findById(id);
    }

    @POST
    @Operation(summary = "Create a new task")
    @APIResponse(responseCode = "201", description = "Task created successfully")
    @APIResponse(responseCode = "400", description = "Validation error")
    public Response create(@Valid TaskRequest request) {
        TaskResponse response = taskService.create(request);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update an existing task")
    @APIResponse(responseCode = "200", description = "Task updated successfully")
    @APIResponse(responseCode = "400", description = "Validation error")
    @APIResponse(responseCode = "404", description = "Task not found")
    public TaskResponse update(@PathParam("id") Long id, @Valid TaskRequest request) {
        return taskService.update(id, request);
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete a task")
    @APIResponse(responseCode = "204", description = "Task deleted successfully")
    @APIResponse(responseCode = "404", description = "Task not found")
    public Response delete(@PathParam("id") Long id) {
        taskService.delete(id);
        return Response.noContent().build();
    }
}
