package com.rejs.reservation.domain.theater.entity;

import com.rejs.reservation.domain.reservation.entity.ReservationSeat;
import com.rejs.reservation.global.entity.BaseEntity;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@SQLDelete(sql = "UPDATE seats SET deleted_at = NOW() WHERE seat_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "seats")
public class Seat extends BaseEntity {
    @Id
    @Tsid
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

    // 생성
    public Seat(Integer rowNum, Integer colNum) {
        this.rowNum = rowNum;
        this.colNum = colNum;
    }

    public Seat(Theater theater, Integer rowNum, Integer colNum) {
        this.mapTheater(theater);
        this.rowNum = rowNum;
        this.colNum = colNum;
    }


    public static Seat create(Theater theater, Integer row, Integer col){
        return new Seat(theater, row, col);
    }
}
