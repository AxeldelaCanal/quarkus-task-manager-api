package com.axeldelacanal.taskmanager.repository;

import com.axeldelacanal.taskmanager.domain.Task;
import com.axeldelacanal.taskmanager.domain.TaskStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class TaskRepository implements PanacheRepository<Task> {

    public List<Task> findAllPaged(int page, int size) {
        return findAll(Sort.by("createdAt").descending()).page(page, size).list();
    }

    public List<Task> findByStatusPaged(TaskStatus status, int page, int size) {
        return find("status", Sort.by("createdAt").descending(), status).page(page, size).list();
    }

    public long countByStatus(TaskStatus status) {
        return count("status", status);
    }
}
