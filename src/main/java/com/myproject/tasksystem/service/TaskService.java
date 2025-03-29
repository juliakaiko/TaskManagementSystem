package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.TaskMapper;
import com.myproject.tasksystem.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TaskService {

    TaskDto createTask(TaskDto taskDto);
    TaskDto getTaskById(Long taskId) ;
    List<TaskDto> getAllTasks();
    Page<TaskDto> getAllTasksNativeWithPagination(Integer page, Integer size);
    List<TaskDto> getTasksByAuthorId(Long authorId);
    List<TaskDto> getTasksByPerformerId(Long assigneeId);
    TaskDto updateTask(Long taskId, TaskDto taskDetails);
    TaskDto changeTaskStatus(Long taskId, String status);
    TaskDto changeTaskPriority(Long taskId, String priority);
    TaskDto changeTaskPerformer(Long taskId, Long performerId);
    TaskDto deleteTask(Long taskId);
}
