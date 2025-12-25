package com.rejs.reservation.domain.screening.service;

import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.dto.*;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.exception.ScreeningExceptionCode;
import com.rejs.reservation.domain.screening.repository.ScreeningQueryRepository;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.exception.TheaterExceptionCode;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Observed
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ScreeningService {
    private final ScreeningQueryRepository screeningQueryRepository;
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

    public Page<ScreeningDto> readScreenings(Pageable pageable) {
        return screeningRepository.findAll(pageable).map(ScreeningDto::from);
    }

    public List<ScreeningWithMovieDto> readScreeningsByTheaterId(Long id, LocalDate date) {
        return screeningQueryRepository.findByTheaterId(id, date);
    }

    public List<ScreeningWithTheaterDto> readScreeningsByMovieId(Long id, LocalDate date) {
        return screeningQueryRepository.findByMovieId(id, date);
    }

    public ScreeningDetailDto readScreeningById(Long id) {
        Screening screening = screeningQueryRepository.findById(id).orElseThrow(() -> new BusinessException(ScreeningExceptionCode.SCREENING_NOT_FOUND));
        List<ScreeningSeatDto> seats = screeningQueryRepository.findScreeningSeats(screening.getId(), screening.getTheaterId());
        return new ScreeningDetailDto(screening, seats);
    }
}
