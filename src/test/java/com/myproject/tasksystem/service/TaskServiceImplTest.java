package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.TaskMapper;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.repository.TaskRepository;
import com.myproject.tasksystem.repository.UserRepository;
import com.myproject.tasksystem.service.impl.TaskServiceImpl;
import com.myproject.tasksystem.util.TaskGenerator;
import com.myproject.tasksystem.util.UserGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class) // Инициализирует моки
public class TaskServiceImplTest {

    @InjectMocks //создает имитирующую реализацию аннотированного типа и внедряет в нее зависимые имитирующие объекты
    private TaskServiceImpl service;

    @Mock // создает фиктивную реализацию для класса
    private TaskRepository taskRepository;

    @Mock // создает фиктивную реализацию для класса
    private UserRepository userRepository;

    private final static Long ENTITY_ID = 1l;

    @Test
    void createTask_ShouldReturnTaskDto() {
        // Arrange
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = service.createTask(taskDto);

        // Assert
        assertNotNull(result);
        assertEquals(taskDto, result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void getTaskById_WhenTaskExists_ShouldReturnTaskDto() {
        // Arrange
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(task));

        // Act
        TaskDto result = service.getTaskById(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(taskDto, result);
    }


    @Test
    void getTaskById_WhenTaskNotExists_ShouldThrowNotFoundException() {
        // Arrange
        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getTaskById(1L));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTaskDtos() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findAll()).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = service.getAllTasks();

        // Assert
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findAll();
    }

    @Test
    void getAllTasksNativeWithPagination_ShouldReturnPageOfTaskDtos() {
        // Arrange
        int page = 0;
        int size = 2;
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        PageRequest pageable = PageRequest.of(page, size, Sort.by("task_id"));
        Page<Task> taskPage = new PageImpl<>(List.of(task), pageable, 1);

        when(taskRepository.findAllTasksNative(pageable)).thenReturn(taskPage);

        // Act
        Page<TaskDto> result = service.getAllTasksNativeWithPagination(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(taskDto, result.getContent().get(0));
        verify(taskRepository).findAllTasksNative(pageable);
    }

    @Test
    void getTasksByAuthorId_ShouldReturnListOfTaskDtos() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findByAuthor_UserId(ENTITY_ID)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = service.getTasksByAuthorId(ENTITY_ID);

        // Assert
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByAuthor_UserId(ENTITY_ID);
    }

    @Test
    void getTasksByPerformerId_ShouldReturnListOfTaskDtos() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findByTaskPerformer_UserId(ENTITY_ID)).thenReturn(List.of(task));

        // Act
        List<TaskDto> result = service.getTasksByPerformerId(ENTITY_ID);

        // Assert
        assertEquals(1, result.size());
        verify(taskRepository, times(1)).findByTaskPerformer_UserId(ENTITY_ID);
    }

    @Test
    void changeTaskStatus_WhenTaskExists_ShouldReturnUpdatedTaskDto() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = service.changeTaskStatus(ENTITY_ID, "NEW_STATUS");

        // Assert
        assertEquals("NEW_STATUS", result.getStatus());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void changeTaskPriority_WhenTaskExists_ShouldReturnUpdatedTaskDto() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = service.changeTaskPriority(ENTITY_ID, "NEW_PRIORITY");

        // Assert
        assertEquals("NEW_PRIORITY", result.getPriority());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void changeTaskPerformer_WhenTaskAndUserExist_ShouldReturnUpdatedTaskDto() {
        // Arrange
        Task task = TaskGenerator.generateTask();
        User user = UserGenerator.generateUser();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        // Act
        TaskDto result = service.changeTaskPerformer(1L, 1L);

        // Assert
        assertNotNull(result.getTaskPerformer());
        assertEquals(1L, result.getTaskPerformer().getUserId());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void changeTaskPerformer_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Arrange
        Task task = TaskGenerator.generateTask();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.changeTaskPerformer(1L, 1L));
    }


    @Test
    void updateTask_WhenTaskExists_ShouldReturnUpdatedTaskDto() {
        // Arrange
        Task existingTask = TaskGenerator.generateTask();

        TaskDto updateDto = new TaskDto();
        updateDto.setTitle("New Title");
        updateDto.setDescription("New Description");
        updateDto.setStatus("NEW_STATUS");
        updateDto.setPriority("HIGH");

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(existingTask);

        // Act
        TaskDto result = service.updateTask(ENTITY_ID, updateDto);

        // Assert
        assertEquals("New Title", result.getTitle());
        assertEquals("New Description", result.getDescription());
        verify(taskRepository, times(1)).save(existingTask);
    }

    @Test
    void deleteTask_WhenTaskExists_ShouldReturnDeletedTaskDto() {
        // Arrange
        Task existingTask = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(existingTask);

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(existingTask));

        // Act
        TaskDto result = service.deleteTask(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(taskDto, result);
        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_WhenTaskNotExists_ShouldThrowNotFoundException() {
        // Arrange
        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.deleteTask(ENTITY_ID));
    }
}
