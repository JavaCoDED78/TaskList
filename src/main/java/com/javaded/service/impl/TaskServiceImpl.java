package com.javaded.service.impl;

import com.javaded.domain.exception.ResourceNotFoundException;
import com.javaded.domain.task.Status;
import com.javaded.domain.task.Task;
import com.javaded.domain.task.TaskImage;
import com.javaded.domain.user.User;
import com.javaded.repository.TaskRepository;
import com.javaded.service.ImageService;
import com.javaded.service.TaskService;
import com.javaded.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ImageService imageService;

    @Override
    @Cacheable(value = "TaskService::getById", key = "#id")
    public Task getById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Task not found with %s: ", id)));
    }

    @Override
    public List<Task> getAllByUserId(Long id) {
        return taskRepository.findAllByUserId(id);
    }

    @Override
    @Transactional
    @CachePut(value = "TaskService::getById", key = "#task.id")
    public Task update(Task task) {
        if (task.getStatus() == null) {
            task.setStatus(Status.TODO);
        }
        taskRepository.save(task);
        return task;
    }

    @Override
    @Transactional
    @Cacheable(value = "TaskService::getAllByUserId", key = "#userId")
    public Task create(Task task, Long userId) {
        User user = userService.getBId(userId);
        task.setStatus(Status.TODO);
        user.getTasks().add(task);
        userService.update(user);
        return task;
    }


    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getAllByUserId", key = "#id")
    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = "TaskService::getById", key = "#id")
    public void uploadImage(Long id, TaskImage image) {
        Task task = getById(id);
        String fileName = imageService.upload(image);
        task.getImages().add(fileName);
        taskRepository.save(task);
    }
}
