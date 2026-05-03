package com.axeldelacanal.taskmanager.service;

import com.axeldelacanal.taskmanager.client.UserServiceClient;
import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.PageResponse;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.exception.UserNotFoundException;
import com.axeldelacanal.taskmanager.exception.UserServiceUnavailableException;
import com.axeldelacanal.taskmanager.repository.TaskRepository;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserServiceClient userServiceClient;

    @InjectMocks
    TaskService taskService;

    private Task sampleTask;
    private static final Long EXISTING_USER_ID = 42L;
    private static final Long MISSING_USER_ID = 99L;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setUserId(EXISTING_USER_ID);
        sampleTask.setTitle("Fix login bug");
        sampleTask.setDescription("Users can't log in with Google OAuth");
        sampleTask.setStatus(TaskStatus.IN_PROGRESS);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    // =========================================================================
    // findAll
    // =========================================================================

    @Test
    @DisplayName("findAll without filter returns paginated tasks from repository")
    void findAll_noFilter_returnsAllTasks() {
        when(taskRepository.findAllPaged(0, 20)).thenReturn(List.of(sampleTask));
        when(taskRepository.count()).thenReturn(1L);

        PageResponse<TaskResponse> result = taskService.findAll(null, 0, 20);

        assertEquals(1, result.content.size());
        assertEquals("Fix login bug", result.content.get(0).title);
        assertEquals(1L, result.total);
        verify(taskRepository).findAllPaged(0, 20);
        verify(taskRepository, never()).findByStatusPaged(any(), anyInt(), anyInt());
    }

    @Test
    @DisplayName("findAll with status filter delegates to findByStatusPaged")
    void findAll_withStatusFilter_delegatesToRepository() {
        when(taskRepository.findByStatusPaged(TaskStatus.IN_PROGRESS, 0, 20)).thenReturn(List.of(sampleTask));
        when(taskRepository.countByStatus(TaskStatus.IN_PROGRESS)).thenReturn(1L);

        PageResponse<TaskResponse> result = taskService.findAll(TaskStatus.IN_PROGRESS, 0, 20);

        assertEquals(1, result.content.size());
        assertEquals(TaskStatus.IN_PROGRESS, result.content.get(0).status);
        verify(taskRepository).findByStatusPaged(TaskStatus.IN_PROGRESS, 0, 20);
        verify(taskRepository, never()).findAllPaged(anyInt(), anyInt());
    }

    // =========================================================================
    // findById
    // =========================================================================

    @Test
    @DisplayName("findById returns mapped response when task exists")
    void findById_existingTask_returnsResponse() {
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id);
        assertEquals(EXISTING_USER_ID, result.userId);
        assertEquals("Fix login bug", result.title);
        assertEquals(TaskStatus.IN_PROGRESS, result.status);
    }

    @Test
    @DisplayName("findById throws TaskNotFoundException when task does not exist")
    void findById_missingTask_throwsNotFoundException() {
        when(taskRepository.findByIdOptional(99L)).thenReturn(Optional.empty());

        TaskNotFoundException ex = assertThrows(
                TaskNotFoundException.class,
                () -> taskService.findById(99L)
        );

        assertTrue(ex.getMessage().contains("99"));
    }

    // =========================================================================
    // create
    // =========================================================================

    @Test
    @DisplayName("create persists new task and defaults status to PENDING when user exists")
    void create_noStatusInRequest_defaultsToPending() {
        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Deploy to staging";
        request.description = "Push latest changes to the staging environment";

        when(userServiceClient.findById(EXISTING_USER_ID))
                .thenReturn(Response.ok().build());
        doNothing().when(taskRepository).persist(any(Task.class));

        TaskResponse result = taskService.create(request);

        assertEquals("Deploy to staging", result.title);
        assertEquals(TaskStatus.PENDING, result.status);
        assertEquals(EXISTING_USER_ID, result.userId);
        verify(taskRepository).persist(any(Task.class));
        verify(userServiceClient).findById(EXISTING_USER_ID);
    }

    @Test
    @DisplayName("create respects explicit status provided in request")
    void create_withExplicitStatus_usesProvidedStatus() {
        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Write migration script";
        request.status = TaskStatus.IN_PROGRESS;

        when(userServiceClient.findById(EXISTING_USER_ID))
                .thenReturn(Response.ok().build());
        doNothing().when(taskRepository).persist(any(Task.class));

        TaskResponse result = taskService.create(request);

        assertEquals(TaskStatus.IN_PROGRESS, result.status);
    }

    @Test
    @DisplayName("create throws UserNotFoundException when user-service returns 404")
    void create_nonExistingUser_throwsUserNotFoundException() {
        TaskRequest request = new TaskRequest();
        request.userId = MISSING_USER_ID;
        request.title = "Orphan task";

        when(userServiceClient.findById(MISSING_USER_ID))
                .thenReturn(Response.status(Response.Status.NOT_FOUND).build());

        UserNotFoundException ex = assertThrows(
                UserNotFoundException.class,
                () -> taskService.create(request)
        );

        assertTrue(ex.getMessage().contains(String.valueOf(MISSING_USER_ID)));
        verify(taskRepository, never()).persist(any(Task.class));
    }

    @Test
    @DisplayName("create throws UserServiceUnavailableException when user-service returns 5xx")
    void create_userServiceReturns500_throwsUnavailableException() {
        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Task during outage";

        when(userServiceClient.findById(EXISTING_USER_ID))
                .thenReturn(Response.serverError().build());

        assertThrows(UserServiceUnavailableException.class, () -> taskService.create(request));
        verify(taskRepository, never()).persist(any(Task.class));
    }

    @Test
    @DisplayName("create throws UserServiceUnavailableException when user-service is unreachable")
    void create_userServiceUnreachable_throwsUnavailableException() {
        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Task while service is down";

        when(userServiceClient.findById(EXISTING_USER_ID))
                .thenThrow(new ProcessingException("Connection refused"));

        assertThrows(UserServiceUnavailableException.class, () -> taskService.create(request));
        verify(taskRepository, never()).persist(any(Task.class));
    }

    // =========================================================================
    // update
    // =========================================================================

    @Test
    @DisplayName("update modifies title, description, and status of existing task")
    void update_existingTask_updatesAllFields() {
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleTask));

        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Fix OAuth bug — resolved";
        request.description = "Issue was in token validation";
        request.status = TaskStatus.DONE;

        TaskResponse result = taskService.update(1L, request);

        assertEquals("Fix OAuth bug — resolved", result.title);
        assertEquals(TaskStatus.DONE, result.status);
    }

    @Test
    @DisplayName("update throws TaskNotFoundException when task does not exist")
    void update_missingTask_throwsNotFoundException() {
        when(taskRepository.findByIdOptional(42L)).thenReturn(Optional.empty());

        TaskRequest request = new TaskRequest();
        request.userId = EXISTING_USER_ID;
        request.title = "Ghost task";

        assertThrows(TaskNotFoundException.class, () -> taskService.update(42L, request));
    }

    // =========================================================================
    // delete
    // =========================================================================

    @Test
    @DisplayName("delete removes task from repository")
    void delete_existingTask_callsRepositoryDelete() {
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        assertDoesNotThrow(() -> taskService.delete(1L));

        verify(taskRepository).delete(sampleTask);
    }

    @Test
    @DisplayName("delete throws TaskNotFoundException when task does not exist")
    void delete_missingTask_throwsNotFoundException() {
        when(taskRepository.findByIdOptional(7L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.delete(7L));
        verify(taskRepository, never()).delete(any(Task.class));
    }
}
