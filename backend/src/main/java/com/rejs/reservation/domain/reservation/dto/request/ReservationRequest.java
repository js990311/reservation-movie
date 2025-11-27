package com.rejs.reservation.domain.reservation.dto.request;

import com.rejs.reservation.domain.theater.dto.SeatDto;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationRequest {
    private Long screeningId;
    private List<Long> seats;

    public ReservationRequest(Long screeningId, List<Long> seats) {
        this.screeningId = screeningId;
        this.seats = seats;
    }
}
