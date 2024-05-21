package com.javaded.service.impl;

import com.javaded.domain.task.Task;
import com.javaded.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Override
    public Task create(Task task, Long userId) {
        return null;
    }

    @Override
    public Task update(Task task) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public Task getById(Long id) {
        return null;
    }

    @Override
    public List<Task> getAllByUserId(Long id) {
        return List.of();
    }
}
