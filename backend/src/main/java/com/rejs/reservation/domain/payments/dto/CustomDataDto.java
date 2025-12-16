package com.rejs.reservation.domain.payments.dto;

import lombok.Getter;

@Getter
public class CustomDataDto {
    private Long reservationId;

    public CustomDataDto(Long reservationId) {
        this.reservationId = reservationId;
    }
}
