package com.rejs.reservation.domain.theater.entity;

import com.rejs.reservation.domain.screening.entity.Screening;
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
@Table(name = "theaters")
public class Theater {
    @Id
    @GeneratedValue
    @Column(name = "theater_id")
    private Long id;

    @Column
    private String name;

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

    public static Theater create(String name, Integer rowSize, Integer colSize){
        Theater theater = new Theater(name);
        for(int row=1;row<=rowSize;row++){
            for(int col=1;col<=colSize;col++){
                Seat seat = new Seat(row, col);
                theater.addSeats(seat);
            }
        }
        return theater;
    }
}
