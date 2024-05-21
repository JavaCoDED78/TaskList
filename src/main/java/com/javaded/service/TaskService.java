package com.javaded.service;

import com.javaded.domain.task.Task;

import java.util.List;

public interface TaskService {

    Task create(Task task, Long userId);

    Task update(Task task);

    void delete(Long id);

    Task getById(Long id);

    List<Task> getAllByUserId(Long id);
}
