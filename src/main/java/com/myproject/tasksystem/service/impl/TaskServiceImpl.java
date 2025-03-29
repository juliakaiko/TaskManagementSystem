package com.myproject.tasksystem.service.impl;

import com.myproject.tasksystem.dto.TaskDto;
import com.myproject.tasksystem.exceptions.NotFoundException;
import com.myproject.tasksystem.mapper.TaskMapper;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.repository.TaskRepository;
import com.myproject.tasksystem.repository.UserRepository;
import com.myproject.tasksystem.service.TaskService;
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
 * Реализация сервиса для работы с задачами.
 * Предоставляет методы для создания, получения, обновления и удаления задач,
 * а также для управления статусами, приоритетами и исполнителями задач.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    private final UserRepository userRepository;

    /**
     * Создает новую задачу на основе переданного DTO.
     *
     * @param taskDto DTO с данными задачи.
     * @return DTO созданной задачи.
     */
    @Override
    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        Task task = TaskMapper.INSTANSE.toEntity(taskDto);
        log.info("createTask(): {}",task);
        task = taskRepository.save(task);
        return  TaskMapper.INSTANSE.toDto(task);
    }

    /**
     * Возвращает задачу по её ID.
     *
     * @param taskId ID задачи.
     * @return DTO найденной задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long taskId) {
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        log.info("getTasksById(): {}",task);
        return TaskMapper.INSTANSE.toDto(task.get());
    }

    /**
     * Возвращает список всех задач.
     *
     * @return Список DTO всех задач.
     */
    @Override
    @Transactional(readOnly = true)
    public List <TaskDto> getAllTasks() {
        List <Task> taskList = taskRepository.findAll();
        log.info("getAllTasks()");
        return taskList.stream().map(TaskMapper.INSTANSE::toDto).toList();
    }

    /**
     * Метод для получения задач с пагинацией и сортировкой.
     *
     * @param page номер страницы (начиная с 0).
     * @param size количество задач на странице.
     * @return страница с задачами (Page<TaskDto>).
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getAllTasksNativeWithPagination(Integer page, Integer size) {
        var pageable  = PageRequest.of(page,size, Sort.by("task_id"));
        Page<Task> taskList = taskRepository.findAllTasksNative(pageable);
        log.info("findAllTasksNativeWithPagination()");
        return taskList.map(TaskMapper.INSTANSE::toDto);
    }


    /**
     * Возвращает список задач, созданных указанным автором.
     *
     * @param authorId ID автора задач.
     * @return Список DTO задач автора.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByAuthorId(Long authorId) {
        List <Task> taskList = taskRepository.findByAuthor_UserId(authorId);
        log.info("getTasksByAuthorId(): {}",authorId);
        return taskList.stream().map(TaskMapper.INSTANSE::toDto).toList();
    }

    /**
     * Возвращает список задач, назначенных указанному исполнителю.
     *
     * @param performerId ID исполнителя задач.
     * @return Список DTO задач исполнителя.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> getTasksByPerformerId(Long performerId) {
        List <Task> taskList = taskRepository.findByTaskPerformer_UserId(performerId);
        List <TaskDto> taskDtoList = taskList.stream().map(TaskMapper.INSTANSE::toDto).toList();
        log.info("getTasksByPerformerId(): {}", performerId);
        return taskDtoList;
    }

    /**
     * Изменяет статус задачи.
     *
     * @param taskId ID задачи.
     * @param status Новый статус задачи.
     * @return DTO обновлённой задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional
    public TaskDto changeTaskStatus(Long taskId, String status) {
        Optional<Task> taskFromDb = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        Task task = taskFromDb.get();
        task.setStatus(status);
        log.info("changeTaskStatus: {}",task);
        taskRepository.save(task);
        return TaskMapper.INSTANSE.toDto(task);
    }

    /**
     * Изменяет приоритет задачи.
     *
     * @param taskId ID задачи.
     * @param priority Новый приоритет задачи.
     * @return DTO обновлённой задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional
    public TaskDto changeTaskPriority(Long taskId, String priority) {
        Optional<Task> taskFromDb = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        Task task = taskFromDb.get();
        task.setPriority(priority);
        log.info("changeTaskPriority: {}",task);
        taskRepository.save(task);
        return TaskMapper.INSTANSE.toDto(task);
    }


    /**
     * Назначает нового исполнителя для задачи.
     *
     * @param taskId ID задачи.
     * @param userId ID пользователя, который будет назначен исполнителем.
     * @return DTO обновлённой задачи.
     * @throws NotFoundException если задача или пользователь с указанными ID не найдены.
     */
    @Override
    @Transactional
    public TaskDto changeTaskPerformer(Long taskId, Long userId) {
        Optional<Task> taskFromDb = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        Task task = taskFromDb.get();
        Optional<User> user = Optional.ofNullable(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User wasn't found with id " + userId)));
        task.setTaskPerformer(user.get());
        log.info("changeTaskPerformer: {}",task);
        taskRepository.save(task);
        return TaskMapper.INSTANSE.toDto(task);
    }

    /**
     * Обновляет данные задачи (название, описание, статус, приоритет и исполнителя).
     *
     * @param taskId ID задачи.
     * @param taskDetails DTO с обновлёнными данными задачи.
     * @return DTO обновлённой задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional
    public TaskDto updateTask(Long taskId, TaskDto taskDetails) {
        Optional<Task> taskFromDb = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        Task task = taskFromDb.get();
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setStatus(taskDetails.getStatus());
        task.setPriority(taskDetails.getPriority());
        task.setTaskPerformer(taskDetails.getTaskPerformer());
        log.info("updateTask(): {}",task);
        taskRepository.save(task);
        return TaskMapper.INSTANSE.toDto(task);
    }

    /**
     * Удаляет задачу по её ID.
     *
     * @param taskId ID задачи.
     * @return DTO удалённой задачи.
     * @throws NotFoundException если задача с указанным ID не найдена.
     */
    @Override
    @Transactional
    public TaskDto deleteTask(Long taskId) {
        Optional<Task> task = Optional.ofNullable(taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task wasn't found with id " + taskId)));
        taskRepository.deleteById(taskId);
        log.info("deleteTask(): {}",task);
        return TaskMapper.INSTANSE.toDto(task.get());
    }
}
