package com.rejs.reservation.domain.user.entity;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String username;

    @Column
    private String password;

    /* 관계 - Reservation */
    @OneToMany(mappedBy = "user")
    private List<Reservation> reservations = new ArrayList<>();


    public void addReservation(Reservation reservation){
        this.reservations.add(reservation);
        reservation.mapUser(this);
    }

    public void removeScreening(Reservation reservation){
        reservations.remove(reservation);
        reservation.mapUser(null);
    }

    // # 생성

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
