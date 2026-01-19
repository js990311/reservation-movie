package com.rejs.reservation.domain.user.dto;

import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.jwt.token.UserToken;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Getter
public class LoginResponse {
    private Tokens tokens;
    private String email;
    private List<String> roles;

    public LoginResponse(Tokens tokens, UserDto userDto) {
        this.tokens = tokens;
        this.email = userDto.getEmail();
        this.roles = Collections.singletonList(userDto.getRole().toString());
    }

    public LoginResponse(Tokens tokens, Authentication authenticate) {
        this.tokens = tokens;
        if(authenticate.getPrincipal() instanceof UserToken userToken){
            this.email = userToken.getUserInfo().getEmail();
            this.roles = Collections.singletonList(userToken.getUserInfo().getRole().toString());
        } else {
            this.email = authenticate.getName();
            this.roles = authenticate.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
        }
    }
}
