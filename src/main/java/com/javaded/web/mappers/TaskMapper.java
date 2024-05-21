package com.javaded.web.mappers;

import com.javaded.domain.task.Task;
import com.javaded.web.dto.task.TaskDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskDto toDto(Task task);

    Task toEntity(TaskDto taskDto);

    List<TaskDto> toDto(List<Task> tasks);
}
