package com.myproject.tasksystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class CommentDto {

    private Long commentId;

    @NotBlank(message = "Comment may not be empty")
    private String text;
}
