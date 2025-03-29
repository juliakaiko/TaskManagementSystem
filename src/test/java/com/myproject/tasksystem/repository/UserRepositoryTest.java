package com.myproject.tasksystem.repository;
import com.myproject.tasksystem.model.User;
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

import java.util.Optional;

@Slf4j
@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    private static User expectedUser;

    @BeforeClass
    public static void setUp(){
        expectedUser = UserGenerator.generateUser();
    }
    
    @Test
    public void findById() {
        this.userRepository.save(expectedUser);
        Optional<User> actualUser = userRepository.findById(1L);
        log.info("Test to find the User with id = 1: "+actualUser.get());
        Assert.assertNotNull(actualUser.get());
        Assert.assertEquals(expectedUser, actualUser.get());
    }

    @Test
    public void findByEmail() {
        this.userRepository.save(expectedUser);
        Optional<User> actualUser = userRepository.findByEmail("user1@mail.ru");
        log.info("Test to find the User with email {} : "+actualUser.get());
        Assert.assertNotNull(actualUser.get());
        Assert.assertEquals(expectedUser, actualUser.get());
    }


    @Test
    public void save() {
        this.userRepository.save(expectedUser);
        Optional<User> actualUser =  userRepository.findById(expectedUser.getUserId());
        log.info("Test to save the User: "+actualUser.get());
        Assertions.assertEquals(expectedUser, actualUser.get());
    }

    @Test
    public void delete() {
        this.userRepository.save(expectedUser);
        User actualUser =  userRepository.findById(expectedUser.getUserId()).get();
        userRepository.delete(actualUser);
        log.info("Test to delete the User with id = 1: "+actualUser);
        Optional<User> deletedUser =userRepository.findById(expectedUser.getUserId());
        Assertions.assertFalse(deletedUser.isPresent());
    }

    @Test
    public void findAll() {
        this.userRepository.save(expectedUser);

        log.info("Test to find all the Users: "+ this.userRepository.findAll());

        Assertions.assertFalse(this.userRepository.findAll().isEmpty(),() -> "List of Users shouldn't be empty");
    }

    @Test
    public void findAllUsersNative() {
        this.userRepository.save(expectedUser);
        var pageable  = PageRequest.of(0,5, Sort.by("User_id"));
        var slice = this.userRepository.findAllUsersNative(pageable);
        slice.forEach(User -> System.out.println(User));
        while (slice.hasNext()){
            slice = this.userRepository.findAllUsersNative(slice.nextPageable());
            slice.forEach(User -> System.out.println(User));
        }
    }
    
}
