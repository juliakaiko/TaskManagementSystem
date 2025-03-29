package com.myproject.tasksystem.util;

import com.myproject.tasksystem.model.Comment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CommentGenerator {

    public static Comment generateComment (){
        return Comment.builder()
                .commentId(1l)
                .text("TEXT")
                .build();
    }

}
