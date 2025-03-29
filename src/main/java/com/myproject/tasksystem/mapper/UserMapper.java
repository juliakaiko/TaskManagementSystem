package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.model.User;
import lombok.NonNull;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANSE = Mappers.getMapper (UserMapper.class);

    @Mapping(target = "userId", source = "user.userId")
    @Mapping(target = "email", source = "user.email")
    @Mapping(target = "password", source = "user.password")
    UserDto toDto(User user);

    /**
     * Конвертирует {@link UserDto} обратно в сущность {@link User}.
     * <p>
     * Автоматически использует обратные правила маппинга, определённые в {@code toDto(User)}.
     * </p>
     *
     * @param userDto DTO-объект комментария для преобразования (не может быть {@code null})
     * @return соответствующая сущность {@link User}
     */
    @InheritInverseConfiguration
    User toEntity (@NonNull UserDto userDto);
}
