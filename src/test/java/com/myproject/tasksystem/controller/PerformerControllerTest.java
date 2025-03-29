package com.myproject.tasksystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.mapper.CommentMapper;
import com.myproject.tasksystem.mapper.TaskMapper;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.service.impl.CommentServiceImpl;
import com.myproject.tasksystem.service.impl.TaskServiceImpl;
import com.myproject.tasksystem.service.impl.UserServiceImpl;
import com.myproject.tasksystem.util.CommentGenerator;
import com.myproject.tasksystem.util.JwtTokenUtils;
import com.myproject.tasksystem.util.TaskGenerator;
import com.myproject.tasksystem.util.UserGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = PerformerController.class)
@Slf4j
@WithMockUser(roles = "USER")// тестрирование с аутентифицированным пользователем
public class PerformerControllerTest {

    private final static Long ENTITY_ID = 1L;

    @MockBean // объект добавляет в Spring ApplicationContext в отличие от @Mock => заменяет бин на мок в контексте
    private TaskServiceImpl taskService;

    @MockBean
    private CommentServiceImpl commentService;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void changeTaskStatus_ValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        String newStatus = "IN PROGRESS";
        Task task = TaskGenerator.generateTask();
        task.setTaskId(ENTITY_ID);
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);
        taskDto.setStatus(newStatus);

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(taskDto);
        when(taskService.getTasksByPerformerId(ENTITY_ID)).thenReturn(List.of(taskDto));
        when(taskService.changeTaskStatus(ENTITY_ID, newStatus)).thenReturn(taskDto);

        // Act & Assert
        mockMvc.perform(put("/api/performer/change_status/{id}/{status}", ENTITY_ID, newStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.status").value(newStatus));
    }

    @Test
    public void changeTaskStatus_InvalidStatus_ShouldReturnBadRequest() throws Exception {
        // Arrange
        String invalidStatus = "INVALID_STATUS";

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(put("/api/performer/change_status/{id}/{status}", ENTITY_ID, invalidStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changeTaskStatus_TaskNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        String newStatus = "IN PROGRESS";

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.put("/api/performer/change_status/{id}/{status}", ENTITY_ID, newStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void changeTaskStatus_UserNotPerformer_ShouldReturnForbidden() throws Exception {
        // Arrange
        String newStatus = "IN PROGRESS";
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);
        userDto.setUserId(ENTITY_ID);

        TaskDto otherTask = new TaskDto();
        otherTask.setTaskId(2L); // ID отличается от ENTITY_ID (1L)

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(new TaskDto());
        when(taskService.getTasksByPerformerId(ENTITY_ID)).thenReturn(List.of(otherTask)); // пользователь имеет другие задачи

        // Act & Assert
        mockMvc.perform(put("/api/performer/change_status/{id}/{status}", ENTITY_ID, newStatus)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createComment_ValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);
        userDto.setUserId(ENTITY_ID);

        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);
        taskDto.setTaskId(ENTITY_ID);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(taskDto);
        when(taskService.getTasksByPerformerId(ENTITY_ID)).thenReturn(List.of(taskDto));
        when(commentService.createComment(any(CommentDto.class), anyLong())).thenReturn(commentDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/performer/create_comment/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(ENTITY_ID))
                .andExpect(jsonPath("$.text").value("TEXT"));
    }

    @Test
    public void createComment_TaskNotFound_ShouldReturnNotFound() throws Exception {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/performer/create_comment/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createComment_UserNotPerformer_ShouldReturnForbidden() throws Exception {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        TaskDto otherTask = new TaskDto();
        otherTask.setTaskId(2L); // ID отличается от ENTITY_ID (1L)

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(new TaskDto());
        when(taskService.getTasksByPerformerId(ENTITY_ID)).thenReturn(List.of(otherTask));

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/performer/create_comment/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    public void createComment_InvalidComment_ShouldReturnBadRequest() throws Exception {
        // Arrange
        CommentDto commentDto = new CommentDto();

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/performer/create_comment/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

   /* @Test
    public void checkUserIsTaskPerformer_UserIsPerformer_ShouldReturnTrue() throws Exception {
        // Arrange
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);
        taskDto.setTaskId(ENTITY_ID);

        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.getUsersByEmail(anyString())).thenReturn(userDto);
        when(taskService.getTaskById(ENTITY_ID)).thenReturn(taskDto);
        when(taskService.getTasksByPerformerId(ENTITY_ID)).thenReturn(List.of(taskDto));
        when(taskService.changeTaskStatus(ENTITY_ID, "PENDING")).thenReturn(taskDto);

        // Act & Assert
        mockMvc.perform(put("/api/performer/change_status/{id}/{status}", ENTITY_ID, "PENDING")
                        .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk());
    }*/

}
