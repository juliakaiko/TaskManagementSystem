package com.myproject.tasksystem.controller;

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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GeneralUserController.class)
@Slf4j
@AutoConfigureMockMvc(addFilters = false) // Отключаем фильтры безопасности для тестов
public class GeneralUserControllerTest {

    private final static Long ENTITY_ID = 1l;

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

    // Task tests
    @Test
    public void getTaskById_ShouldReturnTask_WhenTaskExists() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        Mockito.when(taskService.getTaskById(ENTITY_ID)).thenReturn(taskDto);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.taskId", is(1)))
                .andExpect(jsonPath("$.title", is("TITLE")));
    }

    @Test
    public void getTaskById_ShouldReturnNotFound_WhenTaskNotExists() throws Exception {
        Mockito.when(taskService.getTaskById(ENTITY_ID)).thenReturn(null);

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllTasks_ShouldReturnTaskList() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        Mockito.when(taskService.getAllTasks()).thenReturn(List.of(taskDto));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("TITLE")));
    }

    @Test
    public void getAllTasksWithPagination_ShouldReturnPage() throws Exception {
        Task task = TaskGenerator.generateTask();
        TaskDto taskDto = TaskMapper.INSTANSE.toDto(task);

        Page<TaskDto> page = new PageImpl<>(List.of(taskDto));
        Mockito.when(taskService.getAllTasksNativeWithPagination(0, 2)).thenReturn(page);

        mockMvc.perform(get("/api/pageable_tasks?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].title", is("TITLE")));
    }

    // Comment tests
    @Test
    public void getCommentById_ShouldReturnComment_WhenCommentExists() throws Exception {
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        Mockito.when(commentService.getCommentById(ENTITY_ID)).thenReturn(commentDto);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId", is(1)))
                .andExpect(jsonPath("$.text", is("TEXT")));
    }

    @Test
    public void getCommentById_ShouldReturnNotFound_WhenCommentNotExists() throws Exception {
        Mockito.when(commentService.getCommentById(ENTITY_ID)).thenReturn(null);

        mockMvc.perform(get("/api/comments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllComments_ShouldReturnCommentList() throws Exception {
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        Mockito.when(commentService.getAllComments()).thenReturn(List.of(commentDto));

        mockMvc.perform(get("/api/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text", is("TEXT")));
    }

    // User tests
    @Test
    public void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        Mockito.when(userService.getUsersById(ENTITY_ID)).thenReturn(userDto);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.email", is("user1@mail.ru")));
    }

    @Test
    public void getUserByEmail_ShouldReturnUser_WhenUserExists() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        Mockito.when(userService.getUsersByEmail("user1@mail.ru")).thenReturn(userDto);

        mockMvc.perform(get("/api/users/find_by_email")
                        .param("email", "user1@mail.ru"))  // передаем как query parameter
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("user1@mail.ru")));
    }

    @Test
    public void getAllUsersWithPagination_ShouldReturnPage() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        Page<UserDto> page = new PageImpl<>(List.of(userDto));
        Mockito.when(userService.getAllUsersNativeWithPagination(0, 2)).thenReturn(page);

        mockMvc.perform(get("/api/pageable_users?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].email", is("user1@mail.ru")));
    }
}
