package com.javaded.service.impl;

import com.javaded.domain.exception.ResourceNotFoundException;
import com.javaded.domain.task.Status;
import com.javaded.domain.task.Task;
import com.javaded.domain.task.TaskImage;
import com.javaded.repository.TaskRepository;
import com.javaded.service.ImageService;
import com.javaded.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ImageService imageService;

    @Override
    @Cacheable(
            value = "TaskService::getById",
            key = "#id"
    )
    public Task getById(
            final Long id
    ) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Task not found with %s: ", id))
                );
    }

    @Override
    public List<Task> getAllByUserId(
            final Long id
    ) {
        return taskRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    @CachePut(
            value = "TaskService::getById",
            key = "#task.id"
    )
    public Task update(
            final Task task
    ) {
        Task existing = getById(task.getId());
        if (task.getStatus() == null) {
            existing.setStatus(Status.TODO);
        } else {
            existing.setStatus(task.getStatus());
        }
        existing.setTitle(task.getTitle());
        existing.setDescription(task.getDescription());
        existing.setExpirationDate(task.getExpirationDate());
        taskRepository.save(task);
        return task;
    }

    @Override
    @Transactional
    @Cacheable(
            value = "TaskService::getById",
            condition = "#task.id!=null",
            key = "#task.id"
    )
    public Task create(
            final Task task,
            final Long userId
    ) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        taskRepository.save(task);
        taskRepository.assignTask(userId, task.getId());
        return task;
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "TaskService::getAllByUserId",
            key = "#id"
    )
    public void delete(
            final Long id
    ) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(
            value = "TaskService::getById",
            key = "#id"
    )
    public void uploadImage(
            final Long id,
            final TaskImage image
    ) {
        String fileName = imageService.upload(image);
        taskRepository.addImage(id, fileName);
    }

    @Override
    public List<Task> getAllSoonTasks(
            final Duration duration
    ) {
        LocalDateTime now = LocalDateTime.now();
        return taskRepository.findAllSoonTasks(Timestamp.valueOf(now),
                Timestamp.valueOf(now.plus(duration)));
    }
}
