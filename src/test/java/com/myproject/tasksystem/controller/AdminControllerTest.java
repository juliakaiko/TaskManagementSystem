package com.myproject.tasksystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.CommentMapper;
import com.myproject.tasksystem.mapper.TaskMapper;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.Role;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.service.impl.CommentServiceImpl;
import com.myproject.tasksystem.service.impl.TaskServiceImpl;
import com.myproject.tasksystem.service.impl.UserServiceImpl;
import com.myproject.tasksystem.util.CommentGenerator;
import com.myproject.tasksystem.util.TaskGenerator;
import com.myproject.tasksystem.util.UserGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = AdminController.class)
@Slf4j
//@WithMockUser // тестрирование с аутентифицированным пользователем
public class AdminControllerTest {

    private final static Long ENTITY_ID = 1l;

    @MockBean // объект добавляет в Spring ApplicationContext в отличие от @Mock => заменяет бин на мок в контексте
    private TaskServiceImpl taskService;

    @MockBean
    private CommentServiceImpl commentService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void createTask_ShouldReturnCreatedTask() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        when(taskService.createTask(any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto))) //Преобразования DTO в JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.title").value("TITLE"));
    }

    @Test
    public void createTask_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        TaskDto invalidTaskDto = new TaskDto(); // Пустой DTO или с невалидными данными

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);
        taskDto.setTitle("Updated Task");

        when(taskService.updateTask(eq(ENTITY_ID), any(TaskDto.class))).thenReturn(taskDto);

        mockMvc.perform(put("/api/tasks/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.title").value("Updated Task"));
    }

    @Test
    public void updateTask_ShouldReturnNotFound_WhenTaskNotExists() throws Exception {
        Long taskId = 999L;
        Task task = TaskGenerator.generateTask();
        TaskDto updatedTask = TaskMapper.INSTANSE.toDto(task);

        when(taskService.updateTask(eq(taskId), any(TaskDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/tasks/{id}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateTask_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        TaskDto invalidTaskDto = new TaskDto(); // Пустой DTO или с невалидными данными

        mockMvc.perform(put("/api/tasks/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTaskDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changeTaskStatus_ShouldReturnUpdatedTask() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto updatedTask = TaskMapper.INSTANSE.toDto(task);
        String status = "IN PROGRESS";
        updatedTask.setStatus(status);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(taskService.changeTaskStatus(eq(ENTITY_ID), eq(status))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/change_status/{id}/{status}", ENTITY_ID, status))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.status").value(status));
    }

    @Test
    public void changeTaskStatus_ShouldReturnNotFound_WhenTaskNotExists() throws Exception {
        Long nonExistentTaskId = 999L;
        String status = "IN PROGRESS";

        when(taskService.changeTaskStatus(eq(nonExistentTaskId), eq(status)))
                .thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(put("/api/tasks/change_status/{id}/{status}", nonExistentTaskId, status))
                .andExpect(status().isNotFound());
    }

    @Test
    public void changeTaskStatus_ShouldReturnBadRequest_WhenInvalidStatus() throws Exception {
        String invalidStatus = "INVALID_STATUS";

        mockMvc.perform(put("/api/tasks/change_status/{id}/{status}", ENTITY_ID, invalidStatus))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changeTaskPriority_ShouldReturnUpdatedTask() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto updatedTask = TaskMapper.INSTANSE.toDto(task);
        String priority = "HIGH";
        updatedTask.setPriority(priority);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(taskService.changeTaskPriority(eq(ENTITY_ID), eq(priority))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/change_priority/{id}/{priority}", ENTITY_ID, priority))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.priority").value(priority));
    }

    @Test
    public void changeTaskPriority_ShouldReturnBadRequest_WhenInvalidPriority() throws Exception {
        String invalidPriority = "INVALID_PRIORITY";

        mockMvc.perform(put("/api/tasks/change_priority/{id}/{priority}", ENTITY_ID, invalidPriority))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void changeTaskPerformer_ShouldReturnNotFound_WhenPerformerNotExists() throws Exception {
        Long nonExistentPerformerId = 999L;

        when(taskService.changeTaskPerformer(eq(ENTITY_ID), eq(nonExistentPerformerId)))
                .thenThrow(new NotFoundException("Performer not found"));

        mockMvc.perform(put("/api/tasks/{task_id}/{perfomer_id}", ENTITY_ID, nonExistentPerformerId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void changeTaskPerformer_ShouldReturnUpdatedTask() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto updatedTask = TaskMapper.INSTANSE.toDto(task);

        User performer = UserGenerator.generateUser();
        performer.setUserId(2L);
        Long performer_id = performer.getUserId();
        updatedTask.setTaskPerformer(performer);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(taskService.changeTaskPerformer(eq(ENTITY_ID), eq(performer_id))).thenReturn(updatedTask);

        mockMvc.perform(put("/api/tasks/{task_id}/{perfomer_id}", ENTITY_ID, performer_id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.taskPerformerId").value(performer_id));
    }

    @Test
    public void deleteTask_ShouldReturnTaskDto() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto deletedTask = TaskMapper.INSTANSE.toDto(task);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(taskService.deleteTask(eq(ENTITY_ID))).thenReturn(deletedTask);

        mockMvc.perform(delete("/api/tasks/{id}", ENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId").value(ENTITY_ID))
                .andExpect(jsonPath("$.title").value("TITLE"));
    }

    @Test
    public void deleteTask_ShouldReturnNotFound_WhenTaskNotExists() throws Exception {
        Long nonExistentTaskId = 999L;

        when(taskService.deleteTask(eq(nonExistentTaskId)))
                .thenThrow(new NotFoundException("Task not found"));

        mockMvc.perform(delete("/api/tasks/{id}", nonExistentTaskId))
                .andExpect(status().isNotFound());
    }

    // Comment Tests
    @Test
    public void createComment_ShouldReturnCreatedComment() throws Exception {
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        when(commentService.createComment(any(CommentDto.class), eq(ENTITY_ID))).thenReturn(commentDto);

        mockMvc.perform(post("/api/comments/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto))) //Преобразования DTO в JSON
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(1L))
                .andExpect(jsonPath("$.text").value("TEXT"));
    }


    @Test
    public void createComment_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        CommentDto commentDto = new CommentDto();  // Пустой DTO или с невалидными данными

        mockMvc.perform(post("/api/comments/task/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateComment_ShouldReturnUpdatedComment() throws Exception {
        Comment comment = CommentGenerator.generateComment();
        CommentDto updatedComment = CommentMapper.INSTANSE.toDto(comment);
        updatedComment.setText("Updated comment");

        when(commentService.updateComment(eq(ENTITY_ID), any(CommentDto.class))).thenReturn(updatedComment);

        mockMvc.perform(put("/api/comments/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value( ENTITY_ID))
                .andExpect(jsonPath("$.text").value("Updated comment"));
    }

    @Test
    public void updateComment_ShouldReturnNotFound_WhenCommentNotExists() throws Exception {
        Long commentId = 999L;
        Comment comment = CommentGenerator.generateComment();
        CommentDto updatedComment = CommentMapper.INSTANSE.toDto(comment);

        when(commentService.updateComment(eq(commentId), any(CommentDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/comments/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedComment)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateComment_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        CommentDto invalidCommentDto = new CommentDto(); // Пустой DTO или с невалидными данными

        mockMvc.perform(put("/api/comments/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteComment_ShouldReturnCommentDto() throws Exception {
        Comment comment = CommentGenerator.generateComment();
        CommentDto deletedComment = CommentMapper.INSTANSE.toDto(comment);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(commentService.deleteComment(eq(ENTITY_ID))).thenReturn(deletedComment);

        mockMvc.perform(delete("/api/comments/{id}", ENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(ENTITY_ID))
                .andExpect(jsonPath("$.text").value("TEXT"));
    }

    @Test
    public void deleteComment_ShouldReturnNotFound_WhenTaskNotExists() throws Exception {
        Long nonExistentCommentId = 999L;

        when(commentService.deleteComment(eq(nonExistentCommentId)))
                .thenThrow(new NotFoundException("Comment not found"));

        mockMvc.perform(delete("/api/comments/{id}", nonExistentCommentId))
                .andExpect(status().isNotFound());
    }

    // User Tests
    @Test
    public void createUser_ShouldReturnCreatedUser() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(ENTITY_ID))
                .andExpect(jsonPath("$.email").value("user1@mail.ru"));
    }

    @Test
    public void createUser_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        UserDto userDto = new UserDto ();  // Пустой DTO или с невалидными данными

        mockMvc.perform(post("/api/users", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUser_ShouldReturnUpdatedUser() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto updatedUser = UserMapper.INSTANSE.toDto(user);
        updatedUser.setEmail("newmail@mail.ru");

        when(userService.updateUser(eq(ENTITY_ID), any(UserDto.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/users/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(ENTITY_ID))
                .andExpect(jsonPath("$.email").value("newmail@mail.ru"));
    }

    @Test
    public void updateUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        Long commentId = 999L;
        User user = UserGenerator.generateUser();
        UserDto updatedUser = UserMapper.INSTANSE.toDto(user);

        when(userService.updateUser(eq(ENTITY_ID), any(UserDto.class))).thenReturn(null);

        mockMvc.perform(put("/api/users/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        UserDto invalidUserDto = new UserDto(); // Пустой DTO или с невалидными данными

        mockMvc.perform(put("/api/users/{id}", ENTITY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUserDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteUser_ShouldReturnUserDto() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto deletedUser = UserMapper.INSTANSE.toDto(user);

        //eq — означает, что метод должен быть вызван с аргументом, равным ENTITY_ID и status
        when(userService.deleteUser(eq(ENTITY_ID))).thenReturn(deletedUser);

        mockMvc.perform(delete("/api/users/{id}", ENTITY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(ENTITY_ID))
                .andExpect(jsonPath("$.email").value("user1@mail.ru"));
    }

    @Test
    public void deleteUser_ShouldReturnNotFound_WhenUserNotExists() throws Exception {
        Long nonExistentUsertId = 999L;

        when(userService.deleteUser(eq(nonExistentUsertId)))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(delete("/api/users/{id}", nonExistentUsertId))
                .andExpect(status().isNotFound());
    }
}
