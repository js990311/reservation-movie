package com.rejs.reservation.domain.theater.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.global.entity.BaseEntity;
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
@SQLDelete(sql = "UPDATE theaters SET deleted_at = NOW() WHERE theater_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "theaters")
public class Theater extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long id;

    @Column
    private String name;

    @Column
    private Integer rowSize;

    @Column
    private Integer colSize;

    /* # 관계 - Seat */
    @OneToMany(mappedBy = "theater", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public void addSeats(Seat seat) {
        this.seats.add(seat);
        seat.mapTheater(this);
    }

    public void removeSeats(Seat seat) {
        this.seats.remove(seat);
        seat.mapTheater(null);
    }


    // # 관계 - Screening
    @OneToMany(mappedBy = "theater")
    private List<Screening> screenings = new ArrayList<>();

    public void addScreenings(Screening screening){
        this.screenings.add(screening);
        screening.mapTheater(this);
    }

    public void removeScreenings(Screening screening){
        this.screenings.remove(screening);
        screening.mapTheater(null);
    }


    // 생성

    public Theater(String name) {
        this.name = name;
    }

    public Theater(String name, Integer rowSize, Integer colSize) {
        this.name = name;
        this.rowSize = rowSize;
        this.colSize = colSize;
    }


    public static Theater create(String name, Integer rowSize, Integer colSize){
        Theater theater = new Theater(name, rowSize, colSize);
        for(int row=1;row<=rowSize;row++){
            for(int col=1;col<=colSize;col++){
                Seat seat = new Seat(row, col);
                theater.addSeats(seat);
            }
        }
        return theater;
    }
}
