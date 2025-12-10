package com.rejs.reservation.domain.user.dto;

import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.entity.UserRole;
import lombok.Getter;

import java.util.List;

@Getter
public class UserDto {
    private Long userId;
    private String email;
    private String password;
    private UserRole role;

    public UserDto(Long userId, String email, String password, UserRole role) {
        this.userId = userId;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public static UserDto of(User user){
        return new UserDto(user.getId(), user.getEmail(), user.getPassword(), user.getRole());
    }
}
