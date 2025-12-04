package com.rejs.reservation.domain.reservation.dto;

import lombok.Getter;

@Getter
public class ReservationSeatNumberDto {
    private Integer row;
    private Integer col;

    public ReservationSeatNumberDto(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }
}
