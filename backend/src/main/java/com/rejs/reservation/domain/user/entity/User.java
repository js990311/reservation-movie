package com.rejs.reservation.domain.user.entity;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE users SET deleted_at = NOW() WHERE user_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String name;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    @Column
    private UserRole role;


    // # 생성

    public User(String email, String password) {
        this.email = email;
        this.name = email;
        this.password = password;
        this.role = UserRole.ROLE_USER;
    }

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.name = email;
        this.password = password;
        this.role = role;
    }
}
