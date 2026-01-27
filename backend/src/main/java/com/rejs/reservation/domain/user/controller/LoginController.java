package com.rejs.reservation.domain.user.controller;

import com.rejs.reservation.domain.user.dto.LoginResponse;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.dto.request.RefreshRequest;
import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class LoginController {
    private final LoginService loginService;

    @PostMapping("/login")
    public BaseResponse<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse loginResponse = loginService.login(request);
        return BaseResponse.of(loginResponse);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public BaseResponse<LoginResponse> signup(@RequestBody LoginRequest request){
        LoginResponse loginResponse = loginService.signup(request);
        return BaseResponse.of(loginResponse);
    }

    @PostMapping("/refresh")
    public BaseResponse<LoginResponse> refresh(@RequestBody RefreshRequest request){
        return BaseResponse.of(loginService.refresh(request));
    }
}
