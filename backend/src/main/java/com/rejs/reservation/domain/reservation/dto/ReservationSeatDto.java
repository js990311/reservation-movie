package com.rejs.reservation.domain.reservation.dto;

import com.rejs.reservation.domain.reservation.entity.ReservationSeat;
import lombok.Getter;

@Getter
public class ReservationSeatDto {
    private Long reservationSeatId;
    private Long seatId;
    private Long reservationId;

    public ReservationSeatDto(Long reservationSeatId, Long seatId, Long reservationId) {
        this.reservationSeatId = reservationSeatId;
        this.seatId = seatId;
        this.reservationId = reservationId;
    }

    public static ReservationSeatDto from(ReservationSeat reservationSeat){
        return new ReservationSeatDto(
                reservationSeat.getId(),
                reservationSeat.getScreeningSeat().getId(),
                reservationSeat.getReservationId()
        );
    }
}
