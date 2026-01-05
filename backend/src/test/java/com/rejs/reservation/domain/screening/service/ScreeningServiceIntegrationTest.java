package com.rejs.reservation.domain.screening.service;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class ScreeningServiceIntegrationTest {
    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long movieId;
    private Long theaterId;

    @BeforeEach
    void setup() {
        // 1. 영화 생성
        Movie movie = Movie.builder()
                .title("테스트 영화")
                .duration(120)
                .build();
        movieId = movieRepository.save(movie).getId();

        // 2. 상영관 및 기본 좌석 생성 (이미 검증된 TheaterService 활용)
        TheaterCreateRequest theaterRequest = new TheaterCreateRequest("1관", 5, 5); // 25개 좌석
        TheaterDto theaterDto = theaterService.createTheater(theaterRequest);
        theaterId = theaterDto.getTheaterId();
    }

    @Test
    @DisplayName("상영표 생성 시 상영관의 모든 좌석이 상영 좌석으로 등록되어야 한다")
    void createScreening_success() {
        // Given: 상영표 생성 요청
        LocalDateTime startTime = LocalDateTime.now().plusDays(1);
        CreateScreeningRequest request = new CreateScreeningRequest(theaterId, movieId, startTime);

        // When: 상영표 생성
        ScreeningDto result = screeningService.createScreening(request);

        // Then: 검증
        // 1. 결과 ID 확인
        assertNotNull(result.getScreeningId());

        // 2. JDBC를 통해 screening_seats 테이블에 25개의 좌석이 생성되었는지 확인
        Integer screeningSeatCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM screening_seats WHERE screening_id = ?",
                Integer.class,
                result.getScreeningId()
        );

        // 상영관 좌석이 5x5=25개이므로, 상영 좌석도 25개여야 함
        assertEquals(25, screeningSeatCount);

        // 3. 생성된 상영 좌석의 상태가 'AVAILABLE'인지 확인
        String status = jdbcTemplate.queryForObject(
                "SELECT status FROM screening_seats WHERE screening_id = ? LIMIT 1",
                String.class,
                result.getScreeningId()
        );
        assertEquals("AVAILABLE", status);
    }}