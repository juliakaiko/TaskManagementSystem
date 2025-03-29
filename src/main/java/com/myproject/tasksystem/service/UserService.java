package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.UserDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);
    UserDto getUsersById(Long userId);
    UserDto getUsersByEmail(String email);
    List<UserDto> getAllUsers();
    Page<UserDto> getAllUsersNativeWithPagination(Integer page, Integer size);
    UserDto updateUser(Long userId, UserDto userDetails);
    UserDto deleteUser(Long userId);

}
