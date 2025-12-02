package com.rejs.reservation.domain.theater.dto;

import com.rejs.reservation.domain.theater.entity.Theater;
import lombok.Getter;

import java.util.List;

@Getter
public class TheaterDto {
    private Long theaterId;
    private String name;
    private Integer rowSize;
    private Integer colSize;
    private List<SeatDto> seats;

    public TheaterDto(Long theaterId, String name, Integer rowSize, Integer colSize, List<SeatDto> seats) {
        this.theaterId = theaterId;
        this.name = name;
        this.seats = seats;
        this.rowSize = rowSize;
        this.colSize = colSize;
    }

    public static TheaterDto from(Theater theater){
        return new TheaterDto(
                theater.getId(),
                theater.getName(),
                theater.getRowSize(),
                theater.getColSize(),
                theater.getSeats().stream().map(SeatDto::from).toList()
        );
    }
}
