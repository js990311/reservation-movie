package com.rejs.reservation.domain.reservation.entity;

import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE reservation_seats SET deleted_at = NOW() WHERE reservation_seat_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "reservation_seats")
public class ReservationSeat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_seat_id")
    private ScreeningSeat screeningSeat;

    /* # 관계 - reservation */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public void mapReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public Long getReservationId(){
        return reservation!=null ? reservation.getId() : null;
    }

    public ReservationSeat(ScreeningSeat seat) {
        this.screeningSeat = seat;
    }
}
