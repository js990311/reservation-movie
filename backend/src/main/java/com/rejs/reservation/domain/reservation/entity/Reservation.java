package com.rejs.reservation.domain.reservation.entity;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE reservations SET deleted_at = NOW() WHERE reservation_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "reservations")
public class Reservation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column
    private ReservationStatus status;

    @Column
    private Integer totalAmount;

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

    /* 관계 - Payment */
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Payment> payments = new ArrayList<>();

    public void addPayments(Payment payment){
        this.payments.add(payment);
        payment.mapReservation(this);
    }

    public void removePayments(Payment payment){
        this.payments.remove(payment);
        payment.mapReservation(null);
    }


    // 생성

    public Reservation(Long userId, Long screeningId, Integer totalAmount) {
        this.userId = userId;
        this.screeningId = screeningId;
        this.status = ReservationStatus.PENDING;
        this.totalAmount = totalAmount;
    }

    // 생성
    public static Reservation create(Long userId, Long screeningId, List<ScreeningSeat> screeningSeats){
        int totalAmount = screeningSeats.stream()
                .mapToInt(ScreeningSeat::getPrice)
                .sum();
        Reservation reservation = new Reservation(userId, screeningId, totalAmount);
        for (ScreeningSeat screeningSeat : screeningSeats) {
            // ScreeningSeat 상태 변경
            screeningSeat.reserved();
            // 예약-좌석 매핑 생성 및 리스트에 추가
            reservation.addReservationSeat(new ReservationSeat(screeningSeat));
        }
        return reservation;
    }

    // 로직
    public void confirm(){
        this.status = ReservationStatus.CONFIRMED;
    }

    public void cancel(){
        this.status = ReservationStatus.CANCELED;
    }

}
