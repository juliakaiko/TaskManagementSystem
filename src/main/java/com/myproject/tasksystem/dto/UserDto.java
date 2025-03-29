package com.myproject.tasksystem.dto;

import com.myproject.tasksystem.annotations.EnumNamePattern;
import com.myproject.tasksystem.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserDto {

    private Long userId;

    @Email(regexp="\\w+@\\w+\\.\\w+", message="Please provide a valid email address") //  .+@.+\..+
    @NotBlank(message = "Email address may not be empty")
    private String email;

    @NotBlank (message = "Password may not be empty")
    @Size(min=5, max=255, message = "Password size must be between 5 and 255")
    private String password;

    @EnumNamePattern(regexp = "USER|ADMIN", message = "Role must be either USER or ADMIN")
    @NotNull
    private Role role;
}
