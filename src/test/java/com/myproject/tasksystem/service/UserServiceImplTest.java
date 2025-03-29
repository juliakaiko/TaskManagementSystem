package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.Role;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.repository.UserRepository;
import com.myproject.tasksystem.service.impl.UserServiceImpl;
import com.myproject.tasksystem.util.UserGenerator;
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

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class) // Инициализирует моки
public class UserServiceImplTest {

    @InjectMocks //создает имитирующую реализацию аннотированного типа и внедряет в нее зависимые имитирующие объекты
    private UserServiceImpl service;

    @Mock // создает фиктивную реализацию для класса
    private UserRepository userRepository;

    private final static Long ENTITY_ID = 1l;

    @Test
    void createUser_ShouldReturnUserDto() {
        // Arrange
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto result = service.createUser(userDto);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
        verify (userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUsersById_WhenUserExists_ShouldReturnUserDto() {
        // Arrange
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.of(user));

        // Act
        UserDto result = service.getUsersById(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(userDto, result);
    }

    @Test
    void getUsersById_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getUsersById(ENTITY_ID));
        verify(userRepository, times(1)).findById(ENTITY_ID);
    }

    @Test
    void getUsersByEmail_WhenUserExists_ShouldReturnUserDto() {
        // Arrange
        User user = UserGenerator.generateUser();

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        // Act
        UserDto result = service.getUsersByEmail(user.getEmail());

        // Assert
        assertNotNull(result);
        assertEquals(user.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findByEmail(user.getEmail());
    }

    @Test
    void getUsersByEmail_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Arrange
        User user = UserGenerator.generateUser();
        String email = user.getEmail();

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.getUsersByEmail(email));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        // Arrange
        User user = UserGenerator.generateUser();
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDto> result = service.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getAllUsersNativeWithPagination_ShouldReturnPageOfUserDtos() {
        // Arrange
        int page = 0;
        int size = 2;
        PageRequest pageable = PageRequest.of(page, size, Sort.by("user_id"));

        User user = UserGenerator.generateUser();
        List<User> users = List.of(user);

        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAllUsersNative(pageable)).thenReturn(userPage);

        // Act
        Page<UserDto> result = service.getAllUsersNativeWithPagination(page, size);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository, times(1)).findAllUsersNative(pageable);
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDto() {
        // Arrange
        UserDto updateDto = new UserDto(ENTITY_ID, "updated@example.com", "newpassword", Role.ADMIN);
        User existingUser = UserGenerator.generateUser();

        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // Act
        UserDto result = service.updateUser(ENTITY_ID, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(updateDto.getEmail(), result.getEmail());
        assertEquals(updateDto.getPassword(), result.getPassword());
        assertEquals(updateDto.getRole(), result.getRole());
        verify(userRepository, times(1)).findById(ENTITY_ID);
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Arrange
        UserDto updateDto = new UserDto(ENTITY_ID, "updated@example.com", "newpassword", Role.ADMIN);

        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.updateUser(ENTITY_ID, updateDto));
        verify(userRepository, times(1)).findById(ENTITY_ID);
        verify(userRepository, never()).save(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldReturnDeletedUserDto() {
        // Arrange
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.of(user));

        // Act
        UserDto result = service.deleteUser(ENTITY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(ENTITY_ID, result.getUserId());
        verify(userRepository, times(1)).findById(ENTITY_ID);
        verify(userRepository, times(1)).deleteById(ENTITY_ID);
    }

    @Test
    void deleteUser_WhenUserNotExists_ShouldThrowNotFoundException() {
        // Arrange
        when(userRepository.findById(ENTITY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> service.deleteUser(ENTITY_ID));
        verify(userRepository, times(1)).findById(ENTITY_ID);
        verify(userRepository, never()).deleteById(any());
    }

}
