package com.myproject.tasksystem.repository;

import com.myproject.tasksystem.model.Comment;
import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.util.TaskGenerator;
import com.myproject.tasksystem.util.UserGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;
    private static Task expectedTask;

    @BeforeClass
    public static void setUp(){
        expectedTask = TaskGenerator.generateTask();
    }

    @Test
    public void findById() {
        this.taskRepository.save(expectedTask);
        Optional<Task> actualTask = taskRepository.findById(1L);
        log.info("Test to find the Task with id = 1: "+actualTask.get());
        Assert.assertNotNull(actualTask.get());
        Assert.assertEquals(expectedTask, actualTask.get());
    }

    @Test
    public void findByAuthorId () {
        User user = UserGenerator.generateUser();
        expectedTask.setAuthor(user);
        this.taskRepository.save(expectedTask);
        List <Task> actualTasks = taskRepository.findByAuthor_UserId(user.getUserId());

        log.info("Test to find the lisl of Tasks By AuthorId =: "+user.getUserId());

        Assert.assertEquals(2, actualTasks.size());
        Assert.assertTrue(actualTasks.stream().allMatch(t -> t.getAuthor().getUserId().equals(user.getUserId())));
    }

    @Test
    public void findByPerformerId () {
        User user = UserGenerator.generateUser();
        expectedTask.setTaskPerformer(user);
        this.taskRepository.save(expectedTask);
        List <Task> actualTasks = taskRepository.findByTaskPerformer_UserId(user.getUserId());

        log.info("Test to find the lisl of Tasks By PerformerId =: "+user.getUserId());

        Assert.assertEquals(3, actualTasks.size());
        Assert.assertTrue(actualTasks.stream().allMatch(t -> t.getTaskPerformer().getUserId().equals(user.getUserId())));
    }

    @Test
    public void save() {
        this.taskRepository.save(expectedTask);
        Optional<Task> actualTask =  taskRepository.findById(expectedTask.getTaskId());
        log.info("Test to save the Task: "+actualTask.get());
        Assertions.assertEquals(expectedTask, actualTask.get());
    }

    @Test
    public void delete() {
        this.taskRepository.save(expectedTask);
        Task actualTask =  taskRepository.findById(expectedTask.getTaskId()).get();
        taskRepository.delete(actualTask);
        log.info("Test to delete the Task with id = 1: "+actualTask);
        Optional<Task> deletedTask =taskRepository.findById(expectedTask.getTaskId());
        Assertions.assertFalse(deletedTask.isPresent());
    }

    @Test
    public void findAll() {
        Task savedTask = this.taskRepository.save(expectedTask);
        List<Comment> commentList = new ArrayList<>();
        savedTask.setCommentList(commentList);
        log.info("Test to find all the Tasks: "+ this.taskRepository.findAll());
        Assertions.assertFalse(this.taskRepository.findAll().isEmpty(),() -> "List of Tasks shouldn't be empty");
    }

    @Test
    public void findAllTasksNative() {
        this.taskRepository.save(expectedTask);
        var pageable  = PageRequest.of(0,5, Sort.by("task_id"));
        var slice = this.taskRepository.findAllTasksNative(pageable);
        slice.forEach(Task -> System.out.println(Task));
        while (slice.hasNext()){
            slice = this.taskRepository.findAllTasksNative(slice.nextPageable());
            slice.forEach(Task -> System.out.println(Task));
        }
    }
}
