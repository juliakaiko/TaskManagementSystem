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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Пользователи могут управлять своими задачами, если указаны как исполнитель: менять статус, оставлять комментарии.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/performer")
@Tag(name="PerformerController")
@UserExceptionHandler
@PreAuthorize("hasAuthority('USER')")
@Slf4j
public class PerformerController {

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

    @PutMapping("/change_status/{id}/{status}")
    public ResponseEntity<?> changeTaskStatus (@PathVariable("id") Long taskId,
                                               @PathVariable("status") String status){
        User authenticatedUser = getAuthenticatedUser();
        Long performerId = authenticatedUser.getUserId();
        log.info("Request to change the Status={} for Task with ID={} by Performer={}",status, taskId, authenticatedUser);

        if (!Arrays.asList("PENDING", "IN PROGRESS", "COMPLETED").contains(status.toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        if (taskService.getTaskById(taskId)==null){
            return ResponseEntity.status(404).body("The task wasn't found with id: "+ taskId);
        }

        if (checkUserHaveTaskList (performerId) == false){
            return  ResponseEntity.badRequest().body("This user doesn't have assigned tasks");
        }

        TaskDto  savedTaskDto = null;
        if (checkUserIsTaskPerformer (performerId,taskId) == true){
            savedTaskDto =  taskService.changeTaskStatus(taskId, status);;
        }else{
            return ResponseEntity.status(403).body("This user is not assigned to perform this task with id: "+taskId);
        }

        return ObjectUtils.isEmpty(savedTaskDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedTaskDto);
    }

    /**
     * Только исполнитель конкретной задачи может создать новый комментарий для нее.
     *
     * @param taskId ID задачи, к которой добавляется комментарий (обязательно)
     * @param commentDto DTO с данными комментария (обязательно, должен быть валидным)
     * @apiNote Комментарий не может быть создан без привязки к существующей задаче
     */
    @PostMapping("/create_comment/task/{id}")
    public ResponseEntity<?> createComment (@PathVariable("id") Long taskId,
                                            @RequestBody @Valid CommentDto commentDto){
        User authenticatedUser = getAuthenticatedUser();
        Long performerId = authenticatedUser.getUserId();
        log.info("Request to add new Comment {} by Performer {}",commentDto,authenticatedUser);

        if (taskService.getTaskById(taskId)==null){
            return ResponseEntity.status(404).body("The task wasn't found with id: "+ taskId);
        }

        if (checkUserHaveTaskList (performerId) == false){
            return  ResponseEntity.badRequest().body("This user doesn't have assigned tasks");
        }

        CommentDto  savedCommentDto = null;
        if (checkUserIsTaskPerformer (performerId,taskId) == true){
            savedCommentDto =  commentService.createComment(commentDto,taskId);
        }else{
            return ResponseEntity.status(403).body("This user is not assigned to perform this task with id: "+taskId);
        }

        return ObjectUtils.isEmpty(savedCommentDto)
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(savedCommentDto);
    }

    public Boolean checkUserHaveTaskList (Long performerId){
        Boolean haveTaskList = true;
        List<TaskDto> performerTasks = taskService.getTasksByPerformerId(performerId);
        if (performerTasks == null || performerTasks.isEmpty()) {
            haveTaskList = false;
        }
        return  haveTaskList;
    }

    public Boolean checkUserIsTaskPerformer (Long performerId, Long taskId){
        Boolean isPerformer = false;
        List<TaskDto> performerTasks = taskService.getTasksByPerformerId(performerId);
        for (TaskDto taskDto: performerTasks){
            if (taskDto.getTaskId().equals(taskId)){
                isPerformer = true;
                break;
            }
        }
        return isPerformer;
    }
}
