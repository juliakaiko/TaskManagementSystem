package com.myproject.tasksystem.controller;

import com.myproject.tasksystem.annotations.UserExceptionHandler;
import com.myproject.tasksystem.dto.AuthenticationRequest;
import com.myproject.tasksystem.dto.JwtResponse;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.service.impl.AuthenticationServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/start")
@RequiredArgsConstructor
@UserExceptionHandler
@Tag(name="AuthController") // для swagger-а
@Slf4j
public class AuthController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration(@RequestBody @Valid UserDto request) { //JwtResponse
        log.info("User registration request: {}",request.getEmail());

        if (!Arrays.asList("USER", "ADMIN").contains(request.getRole().getAuthority().toUpperCase())) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        JwtResponse response = authenticationService.registrate(request);

        return ObjectUtils.isEmpty(response)
                ? ResponseEntity.notFound().build() //404
                : ResponseEntity.ok(response);
    }

    @PostMapping("/authentication")
    public ResponseEntity<?> authentication(@RequestBody @Valid AuthenticationRequest request) {
        log.info("User authentication request: {}",request.getEmail());
        JwtResponse response;
        try {
            response = authenticationService.authenticate(request);
        }catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ObjectUtils.isEmpty(response)
                ? ResponseEntity.notFound().build() //404
                : ResponseEntity.ok(response);
    }
}
