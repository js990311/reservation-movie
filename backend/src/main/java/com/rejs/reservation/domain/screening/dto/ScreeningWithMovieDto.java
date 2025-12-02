package com.rejs.reservation.domain.screening.dto;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ScreeningWithMovieDto {
    private Long screeningId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Long movieId;
    private String title;
    private Integer duration;

    public ScreeningWithMovieDto(Long screeningId, Long theaterId, LocalDateTime startTime, LocalDateTime endTime, Long movieId, String title, Integer duration) {
        this.screeningId = screeningId;
        this.theaterId = theaterId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.movieId = movieId;
        this.title = title;
        this.duration = duration;
    }
}
