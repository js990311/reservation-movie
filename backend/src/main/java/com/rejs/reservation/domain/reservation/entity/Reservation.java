package com.rejs.reservation.domain.reservation.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.user.entity.User;
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
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private ReservationStatus status;

    @Column
    private Integer price;

    // # 관계 - User
    @Column(name = "user_id")
    private Long userId;

    // # 관계 상영표

    @Column(name = "screening_id")
    private Long screeningId;

    /* 관계 - ReservationSeat */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSeat> reservationSeats = new ArrayList<>();

    public void addReservationSeat(ReservationSeat reservationSeat){
        this.reservationSeats.add(reservationSeat);
        reservationSeat.mapReservation(this);
    }

    public void removeReservationSeat(ReservationSeat reservationSeat){
        reservationSeats.remove(reservationSeat);
        reservationSeat.mapReservation(null);
    }

    public Reservation(Long userId, Long screeningId, Integer price) {
        this.userId = userId;
        this.screeningId = screeningId;
        this.status = ReservationStatus.PENDING;
        this.price = price;
    }

    // 생성
    public static Reservation create(Long userId, Long screeningId, List<Long> seatIds){
        int price = seatIds.size() * 10000; // 임시로 하드코딩
        Reservation reservation = new Reservation(userId, screeningId, price);

        for(Long seatId : seatIds){
            ReservationSeat rs = new ReservationSeat(seatId);
            rs.assignReservation(reservation);
        }

        return reservation;
    }

}
