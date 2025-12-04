package com.rejs.reservation.domain.reservation.dto;

import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationSummaryDto {
    private Long reservationId;
    private ReservationStatus status;

    private Long screeningId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long movieId;
    private String movieTitle;

    private Long theaterId;
    private String theaterName;
}
