package com.rejs.reservation.domain.screening.service;

import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.exception.ScreeningExceptionCode;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.exception.TheaterExceptionCode;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
    private final MovieRepository movieRepository;
    private final TheaterRepository theaterRepository;

    // CREATE

    @Transactional
    public ScreeningDto createScreening(CreateScreeningRequest request){
        Movie movie = movieRepository.findById(request.getMovieId()).orElseThrow(()-> BusinessException.of(MovieBusinessExceptionCode.MOVIE_NOT_FOUND, request.getMovieId() + " NOT FOUND"));
        Theater theater = theaterRepository.findById(request.getTheaterId()).orElseThrow(() -> BusinessException.of(TheaterExceptionCode.THEATER_NOT_FOUND, request.getTheaterId() + " THEATER NOT FOUND"));
        Screening screening = Screening.of(request.getStartTime(), theater, movie);
        boolean isExists = screeningRepository.existsByScreeningTime(screening.getTheaterId(), screening.getStartTime(), screening.getEndTime());

        if(isExists){
            throw BusinessException.of(ScreeningExceptionCode.SCREENING_TIME_CONFLICT);
        }

        screening = screeningRepository.save(screening);

        return ScreeningDto.from(screening);
    }
}
