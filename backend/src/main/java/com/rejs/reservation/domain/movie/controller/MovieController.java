package com.rejs.reservation.domain.movie.controller;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MoviceService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MoviceService moviceService;

    @PostMapping
    public BaseResponse<MovieDto> createMovie(@RequestBody MovieCreateRequest request){
        MovieDto movie = moviceService.createMovie(request);
        return BaseResponse.of(200, movie);
    }
}
