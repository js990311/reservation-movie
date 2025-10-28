package com.rejs.reservation.domain.reservation.entity;

import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "reservation_seats")
public class ReservationSeat {
    @Id
    @GeneratedValue
    @Column(name = "reservation_seat_id")
    private Long id;

    /* # 관계 - reservation */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Long getReservationId(){
        return reservation!=null ? reservation.getId() : null;
    }

    /**
     * 패키지 내부에서만 사용
     */
    void mapResrvation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void assignReservation(Reservation reservation){
        if(this.reservation != null){
            this.reservation.removeReservationSeat(this);
        }
        if(reservation!= null){
            this.reservation.addReservationSeat(this);
        }
    }

    /* # 관계 - Seat */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    public Long getSeatId(){
        return this.seat != null ? seat.getId() : null;
    }

    /**
     * @deprecated 의존관계 매핑을 위한 헬퍼 메서드에서만 사용하십시오. {@link #assignSeat(Seat)}를 대신 사용하십시오
     */
    @Deprecated
    public void mapSeat(Seat seat){
        this.seat = seat;
    }

    public void assignSeat(Seat seat){
        if(this.seat != null){
            this.seat.removeReservationSeat(this);
        }
        if(seat != null){
            seat.addReservationSeat(this);
        }
    }

}
