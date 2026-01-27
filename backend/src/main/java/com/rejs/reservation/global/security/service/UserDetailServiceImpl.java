package com.rejs.reservation.global.security.service;

import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.exception.UserBusinessExceptionCode;
import com.rejs.reservation.domain.user.service.UserService;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.security.exception.AuthenticationExceptionCode;
import com.rejs.reservation.global.security.jwt.token.UserToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@RequiredArgsConstructor
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user;
        try {
            user = userService.findByUsername(username);
        }catch (BusinessException ex){
            if(ex.getCode().equals(UserBusinessExceptionCode.USER_NOT_FOUND)){
                throw new UsernameNotFoundException("USER NOT FOUND : NAME : " + username);
            }else {
                throw ex;
            }
        }
        return new UserToken(user);
    }

}
