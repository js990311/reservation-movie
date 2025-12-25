package com.rejs.reservation.domain.movie.service;

import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Observed
@RequiredArgsConstructor
@Service
public class MovieService {
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

    @Transactional(readOnly = true)
    public MovieDto readById(Long id){
        Optional<Movie> opt = movieRepository.findById(id);
        Movie movie = opt.orElseThrow(
                ()-> BusinessException.of(MovieBusinessExceptionCode.MOVIE_NOT_FOUND, id + " NOT FOUND"));
        return MovieDto.from(movie);
    }

    public Page<MovieDto> readMovies(Pageable pageable) {
        return movieRepository.findAll(pageable).map(MovieDto::from);
    }
}
