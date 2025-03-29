package com.myproject.tasksystem.controller;

import com.myproject.tasksystem.annotations.UserExceptionHandler;
import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.service.impl.CommentServiceImpl;
import com.myproject.tasksystem.service.impl.TaskServiceImpl;
import com.myproject.tasksystem.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="GeneralUserController")
@UserExceptionHandler
@Slf4j
public class GeneralUserController {

    private final TaskServiceImpl taskService;
    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;

    public User getAuthenticatedUser (){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserDto userDto = userService.getUsersByEmail(currentPrincipalName);
        User user = UserMapper.INSTANSE.toEntity(userDto);
        return user;
    }

    @GetMapping("/")
    public String helloUser () {
        User authenticatedUser = getAuthenticatedUser();
        log.info("helloUser (): {}", authenticatedUser);
        return "Welcome, " + authenticatedUser.getEmail() +"!";
    }

    //Task
    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTaskById (@PathVariable("id") Long id) {
        log.info("Request to find the Task by id: {}",id);
        TaskDto taskDto = taskService.getTaskById(id);
        return ObjectUtils.isEmpty(taskDto) //возвращает true, если объект null или пустой (например, пустая строка, пустая коллекция и т.д.)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(taskDto);
    }

    @GetMapping("/tasks")
    public List<TaskDto> getAllTasks(){
        log.info("Request to find all Tasks: {}");
        return taskService.getAllTasks();
    }

    //Pagination
    @GetMapping("/pageable_tasks")
    //http://localhost:8080/api/pageable_tasks?page=0&size=2
    public Page<TaskDto> getAllNativeTasks(@RequestParam Integer page, @RequestParam Integer size){
        log.info("Request to find all Task with pagination: {}");
        return taskService.getAllTasksNativeWithPagination(page,size);
    }

    @GetMapping("/tasks/author/{id}")
    public List<TaskDto> getTasksByAuthorId (@PathVariable("id") Long authorId) {
        log.info("Request to find all Tasks by authorId: {}",authorId);
        List<TaskDto> taskDtos = taskService.getTasksByAuthorId(authorId);
        return taskDtos;
    }

    @GetMapping("/tasks/performer/{id}")
    public List<TaskDto> getTasksByPerformerId (@PathVariable("id") Long performerId) {
        log.info("Request to find all Tasks by performerId: {}",performerId);
        List<TaskDto> taskDtos = taskService.getTasksByPerformerId(performerId);
        return taskDtos;
    }

    //Comments
    @GetMapping("/comments/{id}")
    public ResponseEntity<?> getCommentById (@PathVariable("id") Long id) {
        log.info("Request to find the Comment by id: {}",id);
        CommentDto commentDto = commentService.getCommentById(id);
        return ObjectUtils.isEmpty(commentDto) //возвращает true, если объект null или пустой (например, пустая строка, пустая коллекция и т.д.)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(commentDto);
    }

    @GetMapping("/comments")
    public List<CommentDto> getAllComments(){
        log.info("Request to find all Comments: {}");
        return commentService.getAllComments();
    }

    @GetMapping("/comments/task/{id}")
    public List<CommentDto> getAllCommentsOfTask(@PathVariable("id") Long id){
        log.info("Request to find all Comments Of Task with id: {}", id);
        return commentService.getAllCommentsOfTask(id);
    }

    //Pagination
    @GetMapping("/pageable_comments")
    //http://localhost:8080/api/pageable_comments?page=0&size=2
    public Page<CommentDto> getAllNativeComments(@RequestParam Integer page, @RequestParam Integer size){
        log.info("Request to find all Comments with pagination: {}");
        return commentService.getAllCommentsNativeWithPagination(page,size);
    }

    //User
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById (@PathVariable("id") Long id) {
        log.info("Request to find the User by id: {}",id);
        UserDto userDto = userService.getUsersById(id);
        return ObjectUtils.isEmpty(userDto) //возвращает true, если объект null или пустой (например, пустая строка, пустая коллекция и т.д.)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(userDto);
    }

    @GetMapping("/users/find_by_email")
    //http://localhost:8080/api/users/find_by_email?email=user1%40yandex.ru
    public ResponseEntity<?> getUserByEmail (@RequestParam ("email") String email) {  //@RequestParam извлекает значения из строки запроса,строка запроса начинается ?
        log.info("Request to find the User by email: {}",email);
        UserDto userDto = userService.getUsersByEmail(email);
        return ObjectUtils.isEmpty(userDto) //возвращает true, если объект null или пустой (например, пустая строка, пустая коллекция и т.д.)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(userDto);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers(){
        log.info("Request to find all Users: {}");
        return userService.getAllUsers();
    }

    //Pagination
    @GetMapping("/pageable_users")
    //http://localhost:8080/api/pageable_users?page=0&size=2
    public Page<UserDto> getAllNativeUsers(@RequestParam Integer page, @RequestParam Integer size){
        log.info("Request to find all Users with pagination: {}");
        return userService.getAllUsersNativeWithPagination(page,size);
    }

}
