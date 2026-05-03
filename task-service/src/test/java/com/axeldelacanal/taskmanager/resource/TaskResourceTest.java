package com.axeldelacanal.taskmanager.resource;

import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.PageResponse;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.service.TaskService;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskResourceTest {

    @Mock
    TaskService taskService;

    @InjectMocks
    TaskResource resource;

    @Test
    @DisplayName("health returns 200 with status UP")
    void health_returns200() {
        Response response = resource.health();
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("getAll returns paginated response from service")
    void getAll_returnsPaginatedResponse() {
        TaskResponse task = new TaskResponse();
        task.id = 1L;
        PageResponse<TaskResponse> page = PageResponse.of(List.of(task), 0, 20, 1L);
        when(taskService.findAll(null, 0, 20)).thenReturn(page);

        PageResponse<TaskResponse> result = resource.getAll(null, 0, 20);

        assertEquals(1, result.content.size());
        assertEquals(1L, result.total);
    }

    @Test
    @DisplayName("getAll clamps size to 100")
    void getAll_clampsMaxSize() {
        PageResponse<TaskResponse> page = PageResponse.of(List.of(), 0, 100, 0L);
        when(taskService.findAll(null, 0, 100)).thenReturn(page);

        resource.getAll(null, 0, 500);

        verify(taskService).findAll(null, 0, 100);
    }

    @Test
    @DisplayName("getById returns task when found")
    void getById_existingTask_returnsTask() {
        TaskResponse task = new TaskResponse();
        task.id = 1L;
        when(taskService.findById(1L)).thenReturn(task);

        TaskResponse result = resource.getById(1L);

        assertEquals(1L, result.id);
    }

    @Test
    @DisplayName("getById propagates TaskNotFoundException")
    void getById_missingTask_propagatesException() {
        when(taskService.findById(99L)).thenThrow(new TaskNotFoundException(99L));

        assertThrows(TaskNotFoundException.class, () -> resource.getById(99L));
    }

    @Test
    @DisplayName("create returns 201 with task body")
    void create_validRequest_returns201() {
        TaskRequest request = new TaskRequest();
        request.userId = 1L;
        request.title = "New task";

        TaskResponse created = new TaskResponse();
        created.id = 1L;
        created.title = "New task";
        when(taskService.create(request)).thenReturn(created);

        Response response = resource.create(request);

        assertEquals(201, response.getStatus());
        TaskResponse body = (TaskResponse) response.getEntity();
        assertEquals("New task", body.title);
    }

    @Test
    @DisplayName("update returns updated task")
    void update_existingTask_returnsUpdated() {
        TaskRequest request = new TaskRequest();
        request.userId = 1L;
        request.title = "Updated";
        request.status = TaskStatus.DONE;

        TaskResponse updated = new TaskResponse();
        updated.id = 1L;
        updated.title = "Updated";
        updated.status = TaskStatus.DONE;
        when(taskService.update(1L, request)).thenReturn(updated);

        TaskResponse result = resource.update(1L, request);

        assertEquals("Updated", result.title);
        assertEquals(TaskStatus.DONE, result.status);
    }

    @Test
    @DisplayName("delete returns 204")
    void delete_existingTask_returns204() {
        doNothing().when(taskService).delete(1L);

        Response response = resource.delete(1L);

        assertEquals(204, response.getStatus());
        verify(taskService).delete(1L);
    }

    @Test
    @DisplayName("delete propagates TaskNotFoundException")
    void delete_missingTask_propagatesException() {
        doThrow(new TaskNotFoundException(99L)).when(taskService).delete(99L);

        assertThrows(TaskNotFoundException.class, () -> resource.delete(99L));
    }
}
