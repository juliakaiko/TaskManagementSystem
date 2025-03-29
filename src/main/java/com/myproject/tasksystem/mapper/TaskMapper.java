package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {

    TaskMapper INSTANSE = Mappers.getMapper (TaskMapper.class);

    @Mapping(target = "taskId", source = "task.taskId")
    @Mapping(target = "title", source = "task.title")
    @Mapping(target = "description", source = "task.description")
    @Mapping(target = "status", source = "task.status")
    @Mapping(target = "priority", source = "task.priority")
    @Mapping(target = "author", source = "task.author")
    @Mapping(target = "taskPerformer", source = "task.taskPerformer")
    @Mapping(target = "commentList", source = "task.commentList")
    TaskDto toDto(Task task);

    /**
     * Конвертирует {@link TaskDto} обратно в сущность {@link Task}.
     * <p>
     * Автоматически использует обратные правила маппинга, определённые в {@code toDto(Task)}.
     * </p>
     *
     * @param taskDto DTO-объект комментария для преобразования (не может быть {@code null})
     * @return соответствующая сущность {@link User}
     */
    @InheritInverseConfiguration
    Task toEntity (@NonNull TaskDto taskDto);
}
