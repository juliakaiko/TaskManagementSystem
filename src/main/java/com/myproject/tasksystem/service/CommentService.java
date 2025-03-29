package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.model.Task;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    CommentDto createComment(CommentDto commentDto, Long taskId);
    CommentDto getCommentById(Long commentId);
    List<CommentDto> getAllComments();
    List<CommentDto> getAllCommentsOfTask(Long taskId);
    Page<CommentDto> getAllCommentsNativeWithPagination(Integer page, Integer size);
    CommentDto updateComment(Long commentId, CommentDto commentDetails);
    CommentDto deleteComment(Long commentId);
}
