package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.CommentMapper;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.repository.CommentRepository;
import com.myproject.tasksystem.repository.TaskRepository;
import com.myproject.tasksystem.service.impl.CommentServiceImpl;
import com.myproject.tasksystem.util.CommentGenerator;
import com.myproject.tasksystem.util.TaskGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class) // Инициализирует моки
public class CommentServiceImplTest {

    @InjectMocks //создает имитирующую реализацию аннотированного типа и внедряет в нее зависимые имитирующие объекты
    private CommentServiceImpl service;

    @Mock // создает фиктивную реализацию для класса
    private CommentRepository commentRepository;

    @Mock // создает фиктивную реализацию для класса
    private TaskRepository taskRepository;

    private final static Long ENTITY_ID = 1l;

    //Comment
    @Test
    public void createComment_whenCorrect_thenReturnCommentDto(){
        // Arrange
        Task task = TaskGenerator.generateTask();
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);
        comment.setTask(task);

        when(taskRepository.findById(task.getTaskId())).thenReturn(Optional.of(task));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // Act
        CommentDto result = service.createComment(commentDto, task.getTaskId());
        verify(commentRepository, times(1)).save(comment);

        log.info("FROM CommentServiceImplTest: save the Comment => comment: "+result);

        // Assert
        assertNotNull(result);
        assertEquals(commentDto, result);

        verify(taskRepository).findById(task.getTaskId());
        //Проверить, что у объекта commentRepository был вызван метод save() с любым аргументом типа Comment ровно один раз
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_ShouldThrowNotFoundException_WhenTaskNotExists() {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.createComment(commentDto, ENTITY_ID));
        verify(taskRepository).findById(ENTITY_ID);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentById_ShouldReturnCommentDto_WhenCommentExists() {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.of(comment));

        // Act
        CommentDto result = service.getCommentById(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(commentDto, result);
        verify(commentRepository).findById(ENTITY_ID);
    }

    @Test
    void getCommentById_ShouldThrowNotFoundException_WhenCommentNotExists() {
        // Arrange
        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getCommentById(ENTITY_ID));
        verify(commentRepository).findById(ENTITY_ID);
    }

    @Test
    void getAllComments_ShouldReturnListOfCommentDtos() {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        when(commentRepository.findAll()).thenReturn(List.of(comment));

        // Act
        List<CommentDto> result = service.getAllComments();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentDto, result.get(0));
        verify(commentRepository).findAll();
    }

    @Test
    void getAllComments_ShouldReturnEmptyList_WhenNoCommentsExist() {
        // Arrange
        when(commentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CommentDto> result = service.getAllComments();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository).findAll();
    }

    @Test
    void getAllCommentsOfTask_ShouldReturnListOfCommentDtos_WhenTaskExists() {
        // Arrange
        Task task = TaskGenerator.generateTask();
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);
        comment.setTask(task);

        task.setCommentList(List.of(comment));

        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.of(task));

        // Act
        List<CommentDto> result = service.getAllCommentsOfTask(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(commentDto, result.get(0));
        verify(taskRepository).findById(ENTITY_ID);
    }

    @Test
    void getAllCommentsOfTask_ShouldThrowNotFoundException_WhenTaskNotExists() {
        // Arrange
        when(taskRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getAllCommentsOfTask(ENTITY_ID));
        verify(taskRepository).findById(ENTITY_ID);
    }

    @Test
    void getAllCommentsNativeWithPagination_ShouldReturnPageOfCommentDtos() {
        // Arrange
        int page = 0;
        int size = 2;

        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        PageRequest pageable = PageRequest.of(page, size, Sort.by("comment_id"));
        Page<Comment> commentPage = new PageImpl<>(List.of(comment), pageable, 1);

        when(commentRepository.findAllCommentsNative(pageable)).thenReturn(commentPage);

        // Act
        Page<CommentDto> result = service.getAllCommentsNativeWithPagination(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(commentDto, result.getContent().get(0));
        verify(commentRepository).findAllCommentsNative(pageable);
    }

    @Test
    void updateComment_ShouldReturnUpdatedCommentDto_WhenCommentExists() {
        // Arrange
        CommentDto commentDetails = new CommentDto();
        commentDetails.setText("Updated text");

        Comment existingComment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(existingComment);

        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(existingComment)).thenReturn(existingComment);

        // Act
        CommentDto result = service.updateComment(ENTITY_ID, commentDetails);

        // Assert
        assertNotNull(result);
        assertEquals(commentDto.getCommentId(), result.getCommentId());
        assertEquals(commentDetails.getText(), result.getText());
        verify(commentRepository).findById(ENTITY_ID);
        verify(commentRepository).save(existingComment);
    }

    @Test
    void updateComment_ShouldThrowNotFoundException_WhenCommentNotExists() {
        // Arrange
        CommentDto commentDetails = new CommentDto();

        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.updateComment(ENTITY_ID, commentDetails));
        verify(commentRepository).findById(ENTITY_ID);
        verify(commentRepository, never()).save(any());
    }

    @Test
    void deleteComment_ShouldReturnDeletedCommentDto_WhenCommentExists() {
        // Arrange
        Comment comment = CommentGenerator.generateComment();
        CommentDto commentDto = CommentMapper.INSTANSE.toDto(comment);

        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).deleteById(ENTITY_ID);

        // Act
        CommentDto result = service.deleteComment(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(ENTITY_ID, result.getCommentId());
        verify(commentRepository).findById(ENTITY_ID);
        verify(commentRepository).deleteById(ENTITY_ID);
    }

    @Test
    void deleteComment_ShouldThrowNotFoundException_WhenCommentNotExists() {
        // Arrange
        when(commentRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.deleteComment(ENTITY_ID));
        verify(commentRepository).findById(ENTITY_ID);
        //Проверяем, что метод deleteById не был вызван (так как комментарий не найден).
        verify(commentRepository, never()).deleteById(any());
    }

}
