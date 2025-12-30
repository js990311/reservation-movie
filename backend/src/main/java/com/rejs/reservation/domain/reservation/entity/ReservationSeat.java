package com.rejs.reservation.domain.reservation.entity;

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
            this.reservation = reservation;
            this.reservation.addReservationSeat(this);
        }
    }

    // # 생성

    public ReservationSeat(Long seatId) {
        this.seatId = seatId;
    }
}
