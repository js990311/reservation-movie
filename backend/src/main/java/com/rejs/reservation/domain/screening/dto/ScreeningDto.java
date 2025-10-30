package com.rejs.reservation.domain.screening.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScreeningDto {
    private Long screeningId;
    private Long theaterId;
    private Long movieId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
