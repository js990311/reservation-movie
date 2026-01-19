package com.rejs.reservation.global.security.jwt.token;

import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserPrincipal {
    private Long userId;
    private String email;
    private UserRole role;

    public static UserPrincipal from(UserDto user){
        return new UserPrincipal(user.getUserId(), user.getEmail(), user.getRole());
    }

}
