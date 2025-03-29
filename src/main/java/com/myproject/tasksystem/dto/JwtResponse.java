package com.myproject.tasksystem.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "Response with access token")
public class JwtResponse {

    private String token;
}
