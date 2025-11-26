package com.rejs.reservation.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.exception.UserBusinessExceptionCode;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import com.rejs.reservation.global.security.service.LoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    private String alreadyUsername = "alreadyUsername";
    private String alreadyPassword = "alreadyPassword";

    @BeforeEach
    void setup(){
        LoginRequest loginRequest = new LoginRequest(alreadyUsername, alreadyPassword);
        loginService.signup(loginRequest);

    }

    @AfterEach
    void clear(){
        userRepository.deleteAll();
    }

    @Test
    void login() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyPassword);
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
        ;
    }

    @Test
    void loginButFailUsername() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", "failUsername", "password", "failPassword");
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        BusinessExceptionCode expectCode = AuthenticationExceptionCode.USER_INFO_MISMATCH;
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(expectCode.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(expectCode.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(expectCode.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/login"))
                .andExpect(jsonPath("$.detail").isString())
        ;
    }

    @Test
    void loginButFailPassword() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyUsername);
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        BusinessExceptionCode expectCode = AuthenticationExceptionCode.USER_INFO_MISMATCH;
        result
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(expectCode.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(expectCode.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(expectCode.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/login"))
                .andExpect(jsonPath("$.detail").isString())
        ;
    }


    @Test
    void signup() throws Exception {
        String username = "username";
        String password = "password";
        Map<String , String> request = Map.of("username", username, "password", password);

        ResultActions result = mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.refreshToken").isString())
        ;
    }

    @Test
    void signupAlreadyExistId() throws Exception {
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyPassword);
        ResultActions result = mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        BusinessExceptionCode expectCode = UserBusinessExceptionCode.USERNAME_ALREADY_EXISTS;
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(expectCode.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(expectCode.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(expectCode.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/signup"))
                .andExpect(jsonPath("$.detail").isString())
        ;
    }

}