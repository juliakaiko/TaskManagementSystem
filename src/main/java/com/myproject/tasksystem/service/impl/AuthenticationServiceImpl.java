package com.myproject.tasksystem.service.impl;

import com.myproject.tasksystem.dto.JwtResponse;
import com.myproject.tasksystem.dto.AuthenticationRequest;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.Role;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.service.AuthenticationService;
import com.myproject.tasksystem.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserServiceImpl userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse registrate(UserDto request) {
        User user = null;
        if (request.getRole().getAuthority().equals("USER")){

            user = User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(request.getPassword()))
                            .role(Role.USER)
                            .build();
            log.info("Request to add new USER: {}",user);
        }

        if (request.getRole().getAuthority().equals("ADMIN")){

            user = User.builder()
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(Role.ADMIN)
                    .build();
            log.info("Request to add new ADMIN: {}",user);
        }

        UserDto userDto = UserMapper.INSTANSE.toDto(user);
        userService.createUser(userDto);
        var jwt = jwtTokenUtils.generateToken(user);
        return new JwtResponse(jwt);
    }

    @Override
    public JwtResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

        log.info("Request to authenticate user: {}",request.getEmail());
        var user = userService.loadUserByUsername(request.getEmail());

        var jwt = jwtTokenUtils.generateToken(user);
        return new JwtResponse(jwt);
    }
}
