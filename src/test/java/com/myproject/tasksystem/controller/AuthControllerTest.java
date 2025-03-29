package com.myproject.tasksystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.tasksystem.dto.AuthenticationRequest;
import com.myproject.tasksystem.dto.JwtResponse;
import com.myproject.tasksystem.dto.UserDto;
import com.myproject.tasksystem.mapper.UserMapper;
import com.myproject.tasksystem.model.User;
import com.myproject.tasksystem.service.impl.AuthenticationServiceImpl;
import com.myproject.tasksystem.util.AuthenticationRequestGenerator;
import com.myproject.tasksystem.util.JwtTokenUtils;
import com.myproject.tasksystem.util.UserGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем фильтры безопасности для тестов
@Slf4j
public class AuthControllerTest {
    private final static Long ENTITY_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private AuthenticationServiceImpl authenticationService;

    @Test
    public void registration_ValidRequest_ReturnsJwtResponse() throws Exception {
        User user = UserGenerator.generateUser();
        UserDto userDto = UserMapper.INSTANSE.toDto(user);

        JwtResponse jwtResponse = new JwtResponse("token");

        when(authenticationService.registrate(userDto)).thenReturn(jwtResponse);

        mockMvc.perform(post("/start/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token")));
    }

    @Test
    public void registration_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        UserDto userDto = new UserDto();

        mockMvc.perform(post("/start/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void authentication_ValidRequest_ReturnsJwtResponse() throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequestGenerator.generateRequest();

        JwtResponse jwtResponse = new JwtResponse("token");

        when(authenticationService.authenticate(authRequest)).thenReturn(jwtResponse);

        mockMvc.perform(post("/start/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", is("token")));
    }

    @Test
    public void authentication_ShouldReturnBadRequest_WhenInvalidInput() throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequestGenerator.generateRequest();
        authRequest.setEmail("invalid_mail");

        mockMvc.perform(post("/start/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest))
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
