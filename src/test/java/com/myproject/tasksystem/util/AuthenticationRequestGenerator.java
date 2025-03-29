package com.myproject.tasksystem.util;

import com.myproject.tasksystem.dto.AuthenticationRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthenticationRequestGenerator {

    public static AuthenticationRequest generateRequest (){
        return AuthenticationRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();
    }
}
