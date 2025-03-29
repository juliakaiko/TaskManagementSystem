package com.myproject.tasksystem.controller;

import com.myproject.tasksystem.annotations.UserExceptionHandler;
import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.service.impl.CommentServiceImpl;
import com.myproject.tasksystem.service.impl.TaskServiceImpl;
import com.myproject.tasksystem.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;


/**
 * Администратор может управлять всеми задачами: создавать новые, редактировать существующие, просматривать и удалять, менять статус и приоритет, назначать исполнителей задачи, оставлять комментарии.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name="AdminController")
@UserExceptionHandler
@PreAuthorize("hasAuthority('ADMIN')")
@Slf4j
public class AdminController {

    private final TaskServiceImpl taskService;
    private final CommentServiceImpl commentService;
    private final UserServiceImpl userService;

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask (@RequestBody @Valid TaskDto taskDto){
        log.info("Request to add new task: {}",taskDto);
        TaskDto savedTaskDto =  taskService.createTask(taskDto);
        return ResponseEntity.ok (savedTaskDto);
    }

    @PutMapping("/tasks/{id}")
    public ResponseEntity <?> updateTask (@PathVariable("id") Long id,
                                          @RequestBody @Valid TaskDto taskDto){
        log.info("Request to update the Task: {}",taskDto);

        TaskDto savedTaskDto =  taskService.updateTask(id,taskDto);

        return ObjectUtils.isEmpty(savedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedTaskDto);
    }

    @PutMapping("/tasks/change_status/{id}/{status}")
    public ResponseEntity <?> changeTaskStatus (@PathVariable("id") Long id,
                                                @PathVariable("status") String status){
        log.info("Request to change the Status={} for Task with ID={} ",status, id);

        if (!Arrays.asList("PENDING", "IN PROGRESS", "COMPLETED").contains(status.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        TaskDto savedTaskDto =  taskService.changeTaskStatus(id, status);

        return ObjectUtils.isEmpty(savedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedTaskDto);
    }

    @PutMapping("/tasks/change_priority/{id}/{priority}")
    public ResponseEntity <?> changeTaskPriority (@PathVariable("id") Long id,
                                                  @PathVariable("priority") String priority){
        log.info("Request to change the Priority={} for Task with ID={}", priority, id);

        if (!Arrays.asList("HIGH", "MEDIUM", "LOW").contains(priority.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid priority value");
        }

        TaskDto savedTaskDto =  taskService.changeTaskPriority(id, priority);

        return ObjectUtils.isEmpty(savedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedTaskDto);
    }

    @PutMapping("/tasks/{task_id}/{perfomer_id}")
    public ResponseEntity <?> changeTaskPerformer (@PathVariable("task_id") Long taskId,
                                                   @PathVariable("perfomer_id") Long userId){
        log.info("Request to change the Performer with ID={} for Task with ID={}",userId, taskId);

        TaskDto savedTaskDto =  taskService.changeTaskPerformer(taskId, userId);

        return ObjectUtils.isEmpty(savedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedTaskDto);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity <?> deleteTask (@PathVariable("id") Long id){
        log.info("Request to delete the Task by id: {}",id);

        TaskDto deletedTaskDto = taskService.deleteTask(id);

        return ObjectUtils.isEmpty(deletedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(deletedTaskDto);
    }

    /**
     * Создает новый комментарий для указанной задачи.
     *
     * @param taskId ID задачи, к которой добавляется комментарий (обязательно)
     * @param commentDto DTO с данными комментария (обязательно, должен быть валидным)
     * @apiNote Комментарий не может быть создан без привязки к существующей задаче
     */
    @PostMapping("/comments/task/{id}")
    public ResponseEntity<?> createComment (@PathVariable("id") Long taskId,
                                            @RequestBody @Valid CommentDto commentDto){
        log.info("Request to add new Comment: {}",commentDto);

        CommentDto savedCommentDto =  commentService.createComment(commentDto, taskId);
        return ResponseEntity.ok (savedCommentDto);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity <?> updateComment (@PathVariable("id") Long id,
                                             @RequestBody @Valid CommentDto commentDto){
        log.info("Request to update the Comment: {}",commentDto);

        CommentDto savedCommentDto =  commentService.updateComment(id,commentDto);

        return ObjectUtils.isEmpty(savedCommentDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedCommentDto);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity <?> deleteComment (@PathVariable("id") Long id){
        log.info("Request to delete the Comment by id: {}",id);

        CommentDto deletedCommentDto = commentService.deleteComment(id);

        return ObjectUtils.isEmpty(deletedCommentDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(deletedCommentDto);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity <?> updateUser (@PathVariable("id") Long id,
                                          @RequestBody @Valid UserDto userDto){
        log.info("Request to update the User: {}",userDto);

        UserDto savedUserDto =  userService.updateUser(id,userDto);

        return ObjectUtils.isEmpty(savedUserDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedUserDto);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity <?> deleteUser (@PathVariable("id") Long id){
        log.info("Request to delete the User by id: {}",id);

        UserDto deletedUserDto = userService.deleteUser(id);

        return ObjectUtils.isEmpty(deletedUserDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(deletedUserDto);
    }

}
