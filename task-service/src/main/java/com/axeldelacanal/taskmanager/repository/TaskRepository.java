package com.axeldelacanal.taskmanager.repository;

import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * Panache repository for {@link Task} entities.
 * Inherits full CRUD from {@link PanacheRepository} and adds status-based filtering.
 */
@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    /**
     * Returns all tasks matching the given status, ordered by creation date descending.
     * Uses Panache's Sort API instead of inline JPQL to keep queries composable.
     */
    public List<Task> findByStatus(TaskStatus status) {
        return list("status", Sort.by("createdAt").descending(), status);
    }
}
