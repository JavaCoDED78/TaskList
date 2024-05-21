package com.javaded.repository;

import com.javaded.domain.task.Task;

import java.util.List;
import java.util.Optional;

public interface TaskRepository {

    Optional<Task> findById(Long id);

    List<Task> findAllByUserId(Long userId);

    void assignToUserById(Long userId, Long taskId);

    void update(Task task);

    void delete(Long id);

    void create(Task task);
}
