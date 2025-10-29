package com.rejs.reservation.domain.movie.service;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.global.exception.GlobalBaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MoviceService {
    private final MovieRepository movieRepository;

    // CREATE

    @Transactional
    public MovieDto createMovie(MovieCreateRequest request){
        Movie movie = Movie.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .build();
        movie = movieRepository.save(movie);
        return MovieDto.from(movie);
    }

    // READ

    @Transactional
    public MovieDto readById(Long id){
        Optional<Movie> opt = movieRepository.findById(id);
        Movie movie = opt.orElseThrow(
                ()-> new GlobalBaseException(HttpStatus.NOT_FOUND));
        return MovieDto.from(movie);
    }
}
