package com.rejs.reservation.domain.user.entity;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    @Column
    private String password;

    @Column
    private String role;

    @Column
    private LocalDateTime createAt;

    // # 생성

    public User(String email, String password) {
        this.email = email;
        this.name = email;
        this.password = password;
        this.role = "USER";
        this.createAt = LocalDateTime.now();
    }
}
