package com.myproject.tasksystem.service;

import com.myproject.tasksystem.dto.AuthenticationRequest;
import com.myproject.tasksystem.dto.JwtResponse;
import com.myproject.tasksystem.dto.UserDto;

public interface AuthenticationService {

    JwtResponse registrate(UserDto request);
    JwtResponse authenticate(AuthenticationRequest request);
}
