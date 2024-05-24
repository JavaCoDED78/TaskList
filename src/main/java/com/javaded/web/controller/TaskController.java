package com.javaded.web.controller;

import com.javaded.domain.task.Task;
import com.javaded.service.TaskService;
import com.javaded.web.dto.task.TaskDto;
import com.javaded.web.dto.validation.OnUpdate;
import com.javaded.web.mappers.TaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Validated
@Tag(name = "Task Controller", description = "Task API")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    @GetMapping("/{id}")
    @Operation(summary = "Get TaskDto by id")
    public TaskDto getById(@PathVariable Long id) {
        Task task = taskService.getById(id);
        return taskMapper.toDto(task);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public void deleteById(@PathVariable Long id) {
        taskService.delete(id);
    }

    @PutMapping
    @Operation(summary = "Update task")
    public TaskDto update(@Validated(OnUpdate.class) TaskDto taskDto) {
        Task task = taskService.update(taskMapper.toEntity(taskDto));
        return taskMapper.toDto(task);
    }
}
