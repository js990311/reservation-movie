package com.rejs.reservation.global.security.jwt.token;

import com.rejs.reservation.domain.user.dto.UserDto;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Collections;

public class UserToken extends User {

    private final UserPrincipal userInfo;
    public UserToken(UserDto userDto) {
        super(userDto.getUserId().toString(), userDto.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(userDto.getRole().name())));
        this.userInfo = UserPrincipal.from(userDto);
    }

    public UserPrincipal getUserInfo() {
        return userInfo;
    }
}
