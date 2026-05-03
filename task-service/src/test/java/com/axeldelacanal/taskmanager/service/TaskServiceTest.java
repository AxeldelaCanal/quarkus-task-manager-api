package com.axeldelacanal.taskmanager.service;

import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import com.axeldelacanal.taskmanager.dto.TaskRequest;
import com.axeldelacanal.taskmanager.dto.TaskResponse;
import com.axeldelacanal.taskmanager.exception.TaskNotFoundException;
import com.axeldelacanal.taskmanager.repository.TaskRepository;
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

    @InjectMocks
    TaskService taskService;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Fix login bug");
        sampleTask.setDescription("Users can't log in with Google OAuth");
        sampleTask.setStatus(TaskStatus.IN_PROGRESS);
        sampleTask.setCreatedAt(LocalDateTime.now());
        sampleTask.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("findAll without filter returns all tasks from repository")
    void findAll_noFilter_returnsAllTasks() {
        when(taskRepository.listAll()).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findAll(null);

        assertEquals(1, result.size());
        assertEquals("Fix login bug", result.get(0).title);
        verify(taskRepository).listAll();
        verifyNoMoreInteractions(taskRepository);
    }

    @Test
    @DisplayName("findAll with status filter delegates to findByStatus")
    void findAll_withStatusFilter_delegatesToRepository() {
        when(taskRepository.findByStatus(TaskStatus.IN_PROGRESS)).thenReturn(List.of(sampleTask));

        List<TaskResponse> result = taskService.findAll(TaskStatus.IN_PROGRESS);

        assertEquals(1, result.size());
        assertEquals(TaskStatus.IN_PROGRESS, result.get(0).status);
        verify(taskRepository).findByStatus(TaskStatus.IN_PROGRESS);
        verify(taskRepository, never()).listAll();
    }

    @Test
    @DisplayName("findById returns mapped response when task exists")
    void findById_existingTask_returnsResponse() {
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponse result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id);
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

    @Test
    @DisplayName("create persists new task and defaults status to PENDING")
    void create_noStatusInRequest_defaultsToPending() {
        TaskRequest request = new TaskRequest();
        request.title = "Deploy to staging";
        request.description = "Push latest changes to the staging environment";

        doNothing().when(taskRepository).persist(any(Task.class));

        TaskResponse result = taskService.create(request);

        assertEquals("Deploy to staging", result.title);
        assertEquals(TaskStatus.PENDING, result.status);
        verify(taskRepository).persist(any(Task.class));
    }

    @Test
    @DisplayName("create respects explicit status provided in request")
    void create_withExplicitStatus_usesProvidedStatus() {
        TaskRequest request = new TaskRequest();
        request.title = "Write migration script";
        request.status = TaskStatus.IN_PROGRESS;

        doNothing().when(taskRepository).persist(any(Task.class));

        TaskResponse result = taskService.create(request);

        assertEquals(TaskStatus.IN_PROGRESS, result.status);
    }

    @Test
    @DisplayName("update modifies title, description, and status of existing task")
    void update_existingTask_updatesAllFields() {
        when(taskRepository.findByIdOptional(1L)).thenReturn(Optional.of(sampleTask));

        TaskRequest request = new TaskRequest();
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
        request.title = "Ghost task";

        assertThrows(TaskNotFoundException.class, () -> taskService.update(42L, request));
    }

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
        verify(taskRepository, never()).delete(any());
    }
}
