package com.rejs.reservation.domain.reservation.dto;

import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationSummaryDto {
    private Long reservationId;
    private ReservationStatus status;
    private Integer totalAmount;

    private Long screeningId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long movieId;
    private String movieTitle;

    private Long theaterId;
    private String theaterName;

    public ReservationSummaryDto(Long reservationId, ReservationStatus status, Integer totalAmount, Long screeningId, LocalDateTime startTime, LocalDateTime endTime, Long movieId, String movieTitle, Long theaterId, String theaterName) {
        this.reservationId = reservationId;
        this.status = status;
        this.totalAmount = totalAmount;
        this.screeningId = screeningId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.theaterId = theaterId;
        this.theaterName = theaterName;
    }
}
