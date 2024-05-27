package com.javaded.web.mappers;

import com.javaded.domain.task.Task;
import com.javaded.web.dto.task.TaskDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskMapper extends Mappable<Task, TaskDto> {
}
