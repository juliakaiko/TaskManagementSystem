package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.model.Comment;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CommentMapper {

    CommentMapper INSTANSE = Mappers.getMapper (CommentMapper.class);

    @Mapping(target = "commentId", source = "comment.commentId")
    @Mapping(target = "text", source = "comment.text")
    CommentDto toDto(Comment comment);

    /**
     * Конвертирует {@link CommentDto} обратно в сущность {@link Comment}.
     * <p>
     * Реализует <b>обратное преобразование</b> относительно маппинга {@code Comment -> CommentDto}.
     * </p>
     *
     * @param commentDto DTO-объект комментария для преобразования (не может быть {@code null})
     * @return соответствующая сущность {@link Comment}
     */
    @InheritInverseConfiguration
    Comment toEntity (@NonNull CommentDto commentDto);

}
