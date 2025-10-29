package com.rejs.reservation.domain.movie.controller;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/movies")
public class MovieController {
    private final MovieService movieService;

    @PostMapping
    public BaseResponse<MovieDto> createMovie(@RequestBody MovieCreateRequest request){
        MovieDto movie = movieService.createMovie(request);
        return BaseResponse.of(201, movie);
    }

    @GetMapping("/{id}")
    public BaseResponse<MovieDto> readMovieById(@PathVariable("id") Long id){
        MovieDto movieDto = movieService.readById(id);
        return BaseResponse.of(200, movieDto);
    }
}
