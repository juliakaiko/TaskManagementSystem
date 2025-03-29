package com.myproject.tasksystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@Schema(description = "Authentication request")
public class AuthenticationRequest {

    @Email(regexp="\\w+@\\w+\\.\\w+", message="Please provide a valid email address") //  .+@.+\..+
    @NotBlank(message = "Email address may not be empty")
    private String email;

    @NotBlank (message = "Password may not be empty")
    @Size(min=5, max=255, message = "Password size must be between 5 and 255")
    private String password;
}
