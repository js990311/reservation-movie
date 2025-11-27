package com.rejs.reservation.domain.theater.entity;

import com.rejs.reservation.domain.reservation.entity.ReservationSeat;
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
@Table(name = "seats")
public class Seat {
    @Id
    @GeneratedValue
    @Column(name = "seat_id")
    private Long id;

    @Column
    private Integer rowNum;

    @Column
    private Integer colNum;

    /* # 관계 - Theater */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id")
    private Theater theater;

    public Long getTheaterId(){
        return theater != null ? theater.getId() : null;
    }

    void mapTheater(Theater theater){
        this.theater = theater;
    }

    public void assignTheater(Theater theater){
        if(this.theater != null){
            theater.removeSeats(this);
        }

        if(theater!=null){
            theater.addSeats(this);
        }
    }

    // 생성

    public Seat(Integer rowNum, Integer colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }
}
