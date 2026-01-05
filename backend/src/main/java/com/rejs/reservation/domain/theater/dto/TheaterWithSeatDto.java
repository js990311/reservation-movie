package com.rejs.reservation.domain.theater.dto;

import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import lombok.Getter;

import java.util.List;

@Getter
public class TheaterWithSeatDto {
    private Long theaterId;
    private String name;
    private Integer rowSize;
    private Integer colSize;
    private List<SeatDto> seats;

    public TheaterWithSeatDto(Long theaterId, String name, Integer rowSize, Integer colSize, List<SeatDto> seats) {
        this.theaterId = theaterId;
        this.name = name;
        this.rowSize = rowSize;
        this.colSize = colSize;
        this.seats = seats;
    }

    public static TheaterWithSeatDto from(Theater theater, List<Seat> seats){
        return new TheaterWithSeatDto(
                theater.getId(),
                theater.getName(),
                theater.getRowSize(),
                theater.getColSize(),
                seats.stream().map(SeatDto::from).toList()
        );
    }
}
