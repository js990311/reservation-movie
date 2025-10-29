package com.rejs.reservation.domain.theater.dto;

import com.rejs.reservation.domain.theater.entity.Seat;
import lombok.Getter;

@Getter
public class SeatDto {
    private Long seatId;
    private Long theaterId;
    private Integer row;
    private Integer col;

    public SeatDto(Long seatId, Long theaterId, Integer row, Integer col) {
        this.seatId = seatId;
        this.theaterId = theaterId;
        this.row = row;
        this.col = col;
    }

    public static SeatDto from(Seat seat){
        return new SeatDto(
            seat.getId(),
            seat.getTheaterId(),
            seat.getRowNum(),
            seat.getColNum()
        );
    }
}
