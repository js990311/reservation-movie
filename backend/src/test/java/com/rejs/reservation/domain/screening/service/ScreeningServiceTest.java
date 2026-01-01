package com.rejs.reservation.domain.screening.service;

import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.screening.repository.ScreeningSeatRepository;
import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ScreeningServiceTest {
    @InjectMocks
    private ScreeningService screeningService;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TheaterRepository theaterRepository;

    @Mock
    private ScreeningRepository screeningRepository;

    @Mock
    private ScreeningSeatRepository screeningSeatRepository;

    @Test
    @DisplayName("상영 회차 생성 시 상영관의 좌석 수만큼 회차별 좌석(ScreeningSeat)이 생성되어야 한다.")
    void createScreening_Success() {
        // Given
        Long movieId = 1L;
        Long theaterId = 1L;
        LocalDateTime startTime = LocalDateTime.of(2026, 1, 1, 14, 0);
        CreateScreeningRequest request = new CreateScreeningRequest(movieId, theaterId, startTime);

        Movie movie = Mockito.mock(Movie.class);
        given(movie.getId()).willReturn(movieId);
        given(movie.getDuration()).willReturn(120);

        // 상영관에 좌석 2개가 있다고 가정
        Theater theater = Mockito.mock(Theater.class);
        Seat seat1 = Mockito.mock(Seat.class);
        Seat seat2 = Mockito.mock(Seat.class);
        given(theater.getId()).willReturn(theaterId);
        given(theater.getSeats()).willReturn(List.of(seat1, seat2));

        given(movieRepository.findById(movieId)).willReturn(Optional.of(movie));
        given(theaterRepository.findWithSeatsById(theaterId)).willReturn(Optional.of(theater));
        given(screeningRepository.existsByScreeningTime(any(), any(), any())).willReturn(false);

        // Screening 저장 시 ID 주입 모킹
        Screening screening = Screening.create(startTime, theater, movie);
        given(screeningRepository.save(any(Screening.class))).willReturn(screening);

        // When
        ScreeningDto result = screeningService.createScreening(request);

        // Then
        assertNotNull(result);
        // 1. ScreeningSeatRepository.saveAll()이 호출되었는지 확인
        verify(screeningSeatRepository, times(1)).saveAll(anyList());
        verify(screeningRepository, times(1)).save(any(Screening.class));
    }

    @Test
    @DisplayName("이미 해당 시간에 상영 회차가 존재하면 예외가 발생한다.")
    void createScreening_ConflictException() {
        // Given
        CreateScreeningRequest request = new CreateScreeningRequest(1L, 1L, LocalDateTime.now());

        Movie movie = Mockito.mock(Movie.class);
        Theater theater = Mockito.mock(Theater.class);

        given(movieRepository.findById(any())).willReturn(Optional.of(movie));
        given(theaterRepository.findWithSeatsById(any())).willReturn(Optional.of(theater));
        // 시간 중복 발생 가정
        given(screeningRepository.existsByScreeningTime(any(), any(), any())).willReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> screeningService.createScreening(request));
    }
}