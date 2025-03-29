package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.util.TaskGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskMapperTest {
    @Test
    public void commentToDTO_whenOk_thenMapFieldsCorrectly() {
        Task task= TaskGenerator.generateTask();
        TaskDto taskDto =TaskMapper.INSTANSE.toDto(task);
        assertEquals(task.getTaskId(),taskDto.getTaskId());
        assertEquals(task.getTitle(),taskDto.getTitle());
        assertEquals(task.getDescription(),taskDto.getDescription());
        assertEquals(task.getStatus(),taskDto.getStatus());
        assertEquals(task.getPriority(),taskDto.getPriority());
        assertEquals(task.getAuthor(),taskDto.getAuthor());
        assertEquals(task.getTaskPerformer(),taskDto.getTaskPerformer());
        assertEquals(task.getCommentList(),taskDto.getCommentList());
    }

    @Test
    public void cimmentDtoToEntity_whenOk_thenMapFieldsCorrectly() {
        Task task= TaskGenerator.generateTask();
        TaskDto taskDto =TaskMapper.INSTANSE.toDto(task);
        task = TaskMapper.INSTANSE.toEntity(taskDto);
        assertEquals(taskDto.getTaskId(),task.getTaskId());
        assertEquals(taskDto.getTitle(),task.getTitle());
        assertEquals(taskDto.getDescription(),task.getDescription());
        assertEquals(taskDto.getStatus(),task.getStatus());
        assertEquals(taskDto.getPriority(),task.getPriority());
        assertEquals(taskDto.getAuthor(),task.getAuthor());
        assertEquals(taskDto.getTaskPerformer(),task.getTaskPerformer());
        assertEquals(taskDto.getCommentList(),task.getCommentList());

    }
}
