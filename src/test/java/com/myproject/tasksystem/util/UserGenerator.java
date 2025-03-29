package com.myproject.tasksystem.util;

import com.myproject.tasksystem.model.Role;
import com.myproject.tasksystem.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass //делает класс статическим и запрещает его наследование.
public class UserGenerator {

    public static User generateUser (){
        return User.builder()
                .userId(1l)
                .email("user1@mail.ru")
                .password("user1")
                .role(Role.USER)
                .build();
    }

}
