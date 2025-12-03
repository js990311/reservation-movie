package com.rejs.reservation.domain.screening.dto;

import com.rejs.reservation.domain.theater.dto.TheaterSummaryDto;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScreeningWithTheaterDto {
    private Long screeningId;
    private Long movieId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long theaterId;
    private String theaterName;

    public ScreeningWithTheaterDto(Long screeningId, Long movieId, LocalDateTime startTime, LocalDateTime endTime, Long theaterId, String theaterName) {
        this.screeningId = screeningId;
        this.movieId = movieId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.theaterId = theaterId;
        this.theaterName = theaterName;
    }
}
