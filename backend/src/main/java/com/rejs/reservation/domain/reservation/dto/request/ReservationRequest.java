package com.rejs.reservation.domain.reservation.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationRequest {
    private Long screeningId;
    private List<Long> seats;
}
