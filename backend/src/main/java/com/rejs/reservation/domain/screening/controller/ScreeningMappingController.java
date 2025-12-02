package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class ScreeningMappingController {
    private final ScreeningService screeningService;

    @GetMapping("/theaters/{id}/screenings")
    public BaseResponse<List<ScreeningDto>> readTheaterScreening(
            @PathVariable("id") Long id,
            @PageableDefault Pageable pageable
    ){
        Page<ScreeningDto> screenings = screeningService.readScreeningsByTheaterId(id, pageable);
        return BaseResponse.ofPage(screenings);
    }

    @GetMapping("/movies/{id}/screenings")
    public BaseResponse<List<ScreeningDto>> readMoviesScreening(
            @PathVariable("id") Long id,
            @PageableDefault Pageable pageable
    ){
        Page<ScreeningDto> screenings = screeningService.readScreeningsByMovieId(id, pageable);
        return BaseResponse.ofPage(screenings);
    }


}
