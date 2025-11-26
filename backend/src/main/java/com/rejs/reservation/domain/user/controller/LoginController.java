package com.rejs.reservation.domain.user.controller;

import com.rejs.reservation.domain.user.dto.request.LoginRequest;
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

    @PostMapping("login")
    public Tokens login(@RequestBody LoginRequest request){
        Tokens tokens = loginService.login(request);
        return tokens;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("signup")
    public ResponseEntity<Tokens> signup(@RequestBody LoginRequest request){
        Tokens tokens = loginService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
    }
}
