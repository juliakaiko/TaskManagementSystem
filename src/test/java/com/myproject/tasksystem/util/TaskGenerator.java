package com.myproject.tasksystem.util;

import com.myproject.tasksystem.model.Task;
import com.myproject.tasksystem.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass //делает класс статическим и запрещает его наследование.
public class TaskGenerator {

    User author = UserGenerator.generateUser();

    public static Task generateTask (){
        return Task.builder()
                .taskId(1l)
                .title("TITLE")
                .description("DESCRIPTION")
                .status("in progress")
                .priority("low")
                .author(author)
                .build();
    }

}
