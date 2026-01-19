package com.rejs.reservation.domain.user.controller;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BusinessExceptionDocs;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.theater.exception.TheaterExceptionCode;
import com.rejs.reservation.domain.user.controller.docs.LoginRequestDocs;
import com.rejs.reservation.domain.user.controller.docs.TokenResponseDocs;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.annotation.Commit;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LoginControllerTest extends AbstractControllerTest {
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    private String alreadyUsername = UUID.randomUUID().toString();
    private String alreadyPassword = "alreadyPassword";

    @BeforeEach
    void setup(){
        LoginRequest loginRequest = new LoginRequest(alreadyUsername, alreadyPassword);
        loginService.signup(loginRequest);

    }


    @Test
    void login() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyPassword);
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").isString())
                .andExpect(jsonPath("$.data.tokens.accessToken.token").isString())
                .andExpect(jsonPath("$.data.tokens.accessToken.expiresAt").isNumber())
                .andExpect(jsonPath("$.data.tokens.refreshToken.token").isString())
                .andExpect(jsonPath("$.data.tokens.refreshToken.expiresAt").isNumber())
        ;
        result.andDo(
                document((docs)->docs
                        .requestSchema(LoginRequestDocs.schema())
                        .requestFields(LoginRequestDocs.fields())
                        .responseSchema(TokenResponseDocs.schema())
                        .responseFields(TokenResponseDocs.fields())
                )
        );
    }

    @Test
    void loginButFailUsername() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", "failUsername", "password", "failPassword");
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        BusinessExceptionCode expectCode = AuthenticationExceptionCode.USER_INFO_MISMATCH;
        andExpectException(()->result, AuthenticationExceptionCode.USER_INFO_MISMATCH, "/login");


        result.andDo(
            documentWithException((docs)->
                    docs
                            .requestSchema(LoginRequestDocs.schema())
                            .requestFields(LoginRequestDocs.fields())
            )
        );
    }

    @Test
    void loginButFailPassword() throws Exception{
        // 회원가입이 이미 완료됨
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyUsername);
        ResultActions result = mockMvc.perform(post("/login").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        BusinessExceptionCode expectCode = AuthenticationExceptionCode.USER_INFO_MISMATCH;
        andExpectException(()->result, AuthenticationExceptionCode.USER_INFO_MISMATCH, "/login");

        result.andDo(
                documentWithException((docs)->
                        docs
                                .requestSchema(LoginRequestDocs.schema())
                                .requestFields(LoginRequestDocs.fields())
                )
        );

    }


    @Test
    void signup() throws Exception {
        String username = UUID.randomUUID().toString();
        String password = "password";
        Map<String , String> request = Map.of("username", username, "password", password);

        ResultActions result = mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email").isString())
                .andExpect(jsonPath("$.data.tokens.accessToken.token").isString())
                .andExpect(jsonPath("$.data.tokens.accessToken.expiresAt").isNumber())
                .andExpect(jsonPath("$.data.tokens.refreshToken.token").isString())
                .andExpect(jsonPath("$.data.tokens.refreshToken.expiresAt").isNumber())
        ;

        result.andDo(
                document((docs)->docs
                        .requestSchema(LoginRequestDocs.schema())
                        .requestFields(LoginRequestDocs.fields())
                        .responseSchema(TokenResponseDocs.schema())
                        .responseFields(TokenResponseDocs.fields())
                )
        );
    }

    @Test
    void signupAlreadyExistId() throws Exception {
        Map<String , String> request = Map.of("username", alreadyUsername, "password", alreadyPassword);
        ResultActions result = mockMvc.perform(post("/signup").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        andExpectException(()->result, UserBusinessExceptionCode.USERNAME_ALREADY_EXISTS, "/signup");

        result.andDo(
                documentWithException((docs)->
                        docs
                                .requestSchema(
                                        LoginRequestDocs.schema()
                                )
                                .requestFields(
                                        LoginRequestDocs.fields()
                                )
                )
        );
    }
}