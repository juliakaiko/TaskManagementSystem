package com.myproject.tasksystem.service.impl;

import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.repository.UserRepository;
import com.myproject.tasksystem.service.UserService;
import com.myproject.tasksystem.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с пользователями.
 * Предоставляет методы для создания, получения, обновления и удаления пользователей,
 * а также для получения списка пользователей с пагинацией.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userFromDb = null;
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()){
            userFromDb = user.get();
        }

        Optional <User> optionalUser = Optional.ofNullable(userFromDb);
        return optionalUser.map(userFromRequest -> new org.springframework.security.core.userdetails.User(
                optionalUser.get().getEmail(),
                optionalUser.get().getPassword(),
                Collections.singleton(optionalUser.get().getRole())
        )).orElseThrow(() -> new UsernameNotFoundException(username+ " not found"));
    }

    /**
     * Создает нового пользователя на основе переданного DTO.
     *
     * @param userDto DTO с данными пользователя.
     * @return DTO созданного пользователя.
     */
    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.INSTANSE.toEntity(userDto);
        log.info("createUser(): {}",user);
        user = userRepository.save(user);
        return UserMapper.INSTANSE.toDto(user);
    }

    /**
     * Возвращает пользователя по его ID.
     *
     * @param userId ID пользователя.
     * @return DTO найденного пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUsersById(Long userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User wasn't found with id " + userId)));
        log.info("getUsersById(): {}",userId);
        return UserMapper.INSTANSE.toDto(user.get());
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email email пользователя.
     * @return DTO найденного пользователя.
     * @throws NotFoundException если пользователь с указанным email не найден.
     */
    @Override
    @Transactional(readOnly = true)
    public UserDto getUsersByEmail(String email) {
        Optional<User> user = Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User wasn't found with email " + email)));
        log.info("getUsersByEmail(): {}",user);
        return UserMapper.INSTANSE.toDto(user.get());
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список DTO всех пользователей.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List <User> userList = userRepository.findAll();
        log.info("getAllUsers()");
        return userList.stream().map(UserMapper.INSTANSE::toDto).toList();
    }

    /**
     * Возвращает страницу с пользователями, используя нативную пагинацию и сортировку по ID.
     *
     * @param page Номер страницы (начиная с 0).
     * @param size Количество пользователей на странице.
     * @return Страница с DTO пользователей.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsersNativeWithPagination(Integer page, Integer size) {
        var pageable  = PageRequest.of(page,size, Sort.by("user_id"));
        Page<User> userList = userRepository.findAllUsersNative(pageable);
        log.info("findAllUsersNativeWithPagination()");
        return userList.map(UserMapper.INSTANSE::toDto);
    }

    /**
     * Обновляет данные пользователя (email, пароль и роль).
     *
     * @param userId ID пользователя.
     * @param userDetails DTO с обновлёнными данными пользователя.
     * @return DTO обновлённого пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDetails) {
        Optional<User> userFromDb = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User wasn't found with id " + userId)));
        User user = userFromDb.get();
        user.setEmail(userDetails.getEmail());
        user.setPassword(userDetails.getPassword());
        user.setRole(userDetails.getRole());
        log.info("updateUser(): {}",user);
        userRepository.save(user);
        return UserMapper.INSTANSE.toDto(user);
    }

    /**
     * Удаляет пользователя по его ID.
     *
     * @param userId ID пользователя.
     * @return DTO удалённого пользователя.
     * @throws NotFoundException если пользователь с указанным ID не найден.
     */
    @Override
    @Transactional
    public UserDto deleteUser(Long userId) {
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User wasn't found with id " + userId)));
        userRepository.deleteById(userId);
        log.info("deleteUser(): {}",user);
        return UserMapper.INSTANSE.toDto(user.get());
    }
}
