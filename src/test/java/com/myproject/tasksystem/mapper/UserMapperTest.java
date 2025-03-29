package com.myproject.tasksystem.mapper;

import com.myproject.tasksystem.dto.CommentDto;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.util.CommentGenerator;
import com.myproject.tasksystem.util.UserGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {

    @Test
    public void commentToDTO_whenOk_thenMapFieldsCorrectly() {
        User user = UserGenerator.generateUser();
        UserDto userDto =UserMapper.INSTANSE.toDto(user);

        assertEquals(user.getUserId(), userDto.getUserId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getPassword(), userDto.getPassword());
    }

    @Test
    public void cimmentDtoToEntity_whenOk_thenMapFieldsCorrectly() {
        User user = UserGenerator.generateUser();
        UserDto userDto =UserMapper.INSTANSE.toDto(user);
        user = UserMapper.INSTANSE.toEntity(userDto);

        assertEquals(userDto.getUserId(), user.getUserId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getPassword(), user.getPassword());
    }
}
