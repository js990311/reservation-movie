package com.rejs.reservation.domain.screening.dto;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.TheaterSummaryDto;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ScreeningDetailDto {
    private boolean disable;
    private ScreeningDto screening;
    private MovieDto movie;
    private TheaterSummaryDto theater;
    private List<ScreeningSeatDto> seats;

    public ScreeningDetailDto(Screening screening, List<ScreeningSeatDto> seats) {
        this.screening = ScreeningDto.from(screening);
        this.movie = MovieDto.from(screening.getMovie());
        this.theater = TheaterSummaryDto.from(screening.getTheater());
        this.seats = seats;
        this.disable = screening.getStartTime().isBefore(LocalDateTime.now());
    }
}
