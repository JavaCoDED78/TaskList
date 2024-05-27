package com.javaded.web.mappers;

import com.javaded.domain.task.TaskImage;
import com.javaded.web.dto.task.TaskImageDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskImageMapper extends Mappable<TaskImage, TaskImageDto> {
}
