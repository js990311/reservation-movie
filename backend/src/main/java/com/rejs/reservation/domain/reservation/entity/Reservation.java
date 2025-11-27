package com.rejs.reservation.domain.reservation.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
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

    /* # 관계 - Screening */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_id")
    private Screening screening;

    public Long getScreeningId(){
        return screening != null ? screening.getId() : null;
    }

    /**
     * @deprecated 의존관계 매핑을 위한 헬퍼 메서드에서만 사용하십시오. {@link #assignScreening(Screening)}를 대신 사용하십시오
     */
    @Deprecated
    public void mapScreening(Screening screening){
        this.screening = screening;
    }

    public void assignScreening(Screening screening){
        if(this.screening != null){
            this.screening.removeScreening(this);
        }
        if(screening != null){
            screening.addReservation(this);
        }
    }


    /* 관계 - User */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public Long getUserId(){
        return user != null ? user.getId() : null;
    }

    /**
     * @deprecated 의존관계 매핑을 위한 헬퍼 메서드에서만 사용하십시오. {@link #assignUser(User)}를 대신 사용하십시오
     */
    @Deprecated
    public void mapUser(User user){
        this.user = user;
    }

    public void assignUser(User user){
        if(this.user != null){
            this.user.removeScreening(this);
        }
        if(user != null){
            user.addReservation(this);
        }
    }


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
}
