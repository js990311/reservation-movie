package com.rejs.reservation.global.security.service;

import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.exception.UserBusinessExceptionCode;
import com.rejs.reservation.domain.user.service.UserService;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LoginService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Transactional
    public Tokens signup(LoginRequest request) {
        String encryptPassword = passwordEncoder.encode(request.getPassword());
        UserDto user = userService.createUser(request.getUsername(), encryptPassword);
        return jwtUtils.generateToken(user.getUserId().toString(), Collections.singletonList(user.getRole().name()));
    }

    public Tokens login(LoginRequest request) {
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            // 로그인 실패하면 예외를 발생시킴
            return jwtUtils.generateToken(authenticate.getName(), authenticate.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        }catch (BusinessException ex){
            if(ex.getCode().equals(UserBusinessExceptionCode.USER_NOT_FOUND)){
                throw BusinessException.of(AuthenticationExceptionCode.USER_INFO_MISMATCH, ex);
            }else {
                throw ex;
            }
        }catch (BadCredentialsException ex){ // BadCredentialException
            throw BusinessException.of(AuthenticationExceptionCode.USER_INFO_MISMATCH, ex);
        }catch (RuntimeException ex){
            throw ex;
        }
    }
}
