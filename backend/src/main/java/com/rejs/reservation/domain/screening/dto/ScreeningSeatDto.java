package com.rejs.reservation.domain.screening.dto;

import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import lombok.Getter;

@Getter
public class ScreeningSeatDto {
    private Long seatId;
    private Integer row;
    private Integer col;
    private boolean isReserved;

    public ScreeningSeatDto(Long seatId, Integer row, Integer col, boolean isReserved) {
        this.seatId = seatId;
        this.row = row;
        this.col = col;
        this.isReserved = isReserved; // null이 아니면서 Canceled상태가 아닌 경우
    }
}
