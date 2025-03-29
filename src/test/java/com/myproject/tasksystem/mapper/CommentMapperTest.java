package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.util.CommentGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {
    @Test
    public void commentToDTO_whenOk_thenMapFieldsCorrectly() {
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto =CommentMapper.INSTANSE.toDto(comment);
        assertEquals(comment.getCommentId(), commentDto.getCommentId());
        assertEquals(comment.getText(), commentDto.getText());
    }

    @Test
    public void cimmentDtoToEntity_whenOk_thenMapFieldsCorrectly() {
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto =CommentMapper.INSTANSE.toDto(comment);
        comment = CommentMapper.INSTANSE.toEntity(commentDto);
        assertEquals(commentDto.getCommentId(), comment.getCommentId());
        assertEquals(commentDto.getText(), comment.getText());
    }
}
