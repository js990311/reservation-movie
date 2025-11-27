package com.rejs.reservation.domain.user.dto;

import com.rejs.reservation.domain.user.entity.User;
import lombok.Getter;

@Getter
public class UserDto {
    private Long userId;
    private String email;
    private String password;

    public UserDto(Long userId, String email, String password) {
        this.userId = userId;
        this.email = email;
        this.password = password;
    }

    public static UserDto of(User user){
        return new UserDto(user.getId(), user.getEmail(), user.getPassword());
    }
}
