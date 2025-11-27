package com.rejs.reservation.domain.reservation.entity;

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

    // # 관계 - seat : DDD 기반을 위해서 어그리게이트 외부에 있는 seat과의 연결 제외
    @Column(name = "seat_id")
    private Long seatId;

    public void assignSeat(Long seatId) {
        this.seatId = seatId;
    }

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
    void mapReservation(Reservation reservation) {
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
}
