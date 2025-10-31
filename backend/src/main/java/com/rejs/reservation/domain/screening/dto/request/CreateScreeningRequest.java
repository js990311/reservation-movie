package com.rejs.reservation.domain.screening.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class CreateScreeningRequest {
    private Long theaterId;
    private Long movieId;
    private LocalDateTime startTime;
}
