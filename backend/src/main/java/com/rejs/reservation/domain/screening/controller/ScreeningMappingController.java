package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.ScreeningWithMovieDto;
import com.rejs.reservation.domain.screening.dto.ScreeningWithTheaterDto;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class ScreeningMappingController {
    private final ScreeningService screeningService;

    @GetMapping("/theaters/{id}/screenings")
    public BaseResponse<List<ScreeningWithMovieDto>> readTheaterScreening(
            @PathVariable("id") Long id,
            @RequestParam(value = "date",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        List<ScreeningWithMovieDto> screenings = screeningService.readScreeningsByTheaterId(id, date);
        return BaseResponse.ofList(screenings);
    }

    @GetMapping("/movies/{id}/screenings")
    public BaseResponse<List<ScreeningWithTheaterDto>> readMoviesScreening(
            @PathVariable("id") Long id,
            @RequestParam(value = "date",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        List<ScreeningWithTheaterDto> screenings = screeningService.readScreeningsByMovieId(id, date);
        return BaseResponse.ofList(screenings);
    }


}
