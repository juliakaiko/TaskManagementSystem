package com.myproject.tasksystem.service.impl;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.CommentMapper;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.repository.CommentRepository;
import com.myproject.tasksystem.repository.TaskRepository;
import com.myproject.tasksystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с комментариями задач.
 * Предоставляет методы для создания, получения, обновления и удаления комментариев,
 * а также для получения списка комментариев с пагинацией.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final TaskRepository taskRepository;

    /**
     * Создает новый комментарий для указанной задачи.
     *
     * @param commentDto DTO с данными комментария.
     * @param taskId     ID задачи, к которой относится комментарий.
     * @return DTO созданного комментария.
     * @throws NotFoundException если задача с указанным ID не найдена.
     * @apiNote Комментарий не может быть создан без привязки к существующей задаче
     */
    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long taskId) {
        Comment comment = CommentMapper.INSTANSE.toEntity(commentDto);
        log.info("createComment(): {}",comment);
        Optional<Task> taskFromDb = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        comment.setTask(taskFromDb.get());
        comment = commentRepository.save(comment);
        return CommentMapper.INSTANSE.toDto(comment);
    }

    /**
     * Возвращает комментарий по его ID.
     *
     * @param commentId ID комментария.
     * @return DTO найденного комментария.
     * @throws NotFoundException если комментарий с указанным ID не найден.
     */
    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        Optional<Comment> comment = Optional.ofNullable(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment wasn't found with id " + commentId)));
        log.info("getCommentsById(): {}",comment);
        return CommentMapper.INSTANSE.toDto(comment.get());
    }

    /**
     * Возвращает список всех комментариев.
     *
     * @return Список DTO всех комментариев.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllComments() {
        List <Comment> commentList = commentRepository.findAll();
        log.info("getAllComments()");
        return commentList.stream().map(CommentMapper.INSTANSE::toDto).toList();
    }

    /**
     * Возвращает список всех комментариев для указанной задачи.
     *
     * @param taskId ID задачи.
     * @return Список DTO комментариев задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getAllCommentsOfTask(Long taskId) {
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        List <Comment> commentList = task.get().getCommentList();
        log.info("getAllCommentsOfTask()");
        return commentList.stream().map(CommentMapper.INSTANSE::toDto).toList();
    }

    /**
     * Возвращает страницу с комментариями, используя нативную пагинацию.
     *
     * @param page Номер страницы (начиная с 0).
     * @param size Количество элементов на странице.
     * @return Страница с DTO комментариев.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CommentDto> getAllCommentsNativeWithPagination(Integer page, Integer size) {
        var pageable  = PageRequest.of(page,size, Sort.by("comment_id"));
        Page<Comment> commentList = commentRepository.findAllCommentsNative(pageable);
        log.info("findAllCommentsNativeWithPagination()");
        return commentList.map(CommentMapper.INSTANSE::toDto);
    }

    /**
     * Обновляет текст комментария.
     *
     * @param commentId      ID комментария.
     * @param commentDetails DTO с новыми данными комментария.
     * @return DTO обновленного комментария.
     * @throws NotFoundException если комментарий с указанным ID не найден.
     */
    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDetails) {
        Optional<Comment> commentFromDb = Optional.ofNullable(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment wasn't found with id " + commentId)));
        Comment comment = commentFromDb.get();
        comment.setText(commentDetails.getText());
        log.info("updateComment(): {}",comment);
        commentRepository.save(comment);
        return CommentMapper.INSTANSE.toDto(comment);
    }

    /**
     * Удаляет комментарий по его ID.
     *
     * @param commentId ID комментария.
     * @return DTO удаленного комментария.
     * @throws NotFoundException если комментарий с указанным ID не найден.
     */
    @Override
    @Transactional
    public CommentDto deleteComment(Long commentId) {
        Optional<Comment> comment = Optional.ofNullable(commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment wasn't found with id " + commentId)));
        commentRepository.deleteById(commentId);
        log.info("deleteComment(): {}",comment);
        return CommentMapper.INSTANSE.toDto(comment.get());
    }
}
