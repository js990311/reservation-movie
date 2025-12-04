package com.rejs.reservation.domain.reservation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationDetailDto {
    private ReservationSummaryDto reservation;
    private List<ReservationSeatNumberDto> seats;

    public ReservationDetailDto(ReservationSummaryDto reservation, List<ReservationSeatNumberDto> seats) {
        this.reservation = reservation;
        this.seats = seats;
    }
}
