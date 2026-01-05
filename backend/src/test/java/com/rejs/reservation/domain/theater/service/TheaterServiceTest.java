package com.rejs.reservation.domain.theater.service;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class TheaterServiceTest {
    @Autowired
    private TheaterService theaterService;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("상영관 생성 시 입력한 사이즈만큼 좌석이 함께 생성되어야 한다")
    void createTheater() {
        // 1. Given: 10x10 사이즈의 상영관 생성 요청
        String theaterName = "영화관";
        int rowSize = 10;
        int colSize = 10;
        TheaterCreateRequest request = new TheaterCreateRequest(theaterName, rowSize, colSize);

        // 2. When: 상영관 생성 서비스 호출
        TheaterDto result = theaterService.createTheater(request);

        // 3. Then: 검증
        // 3-1. 상영관 기본 정보 확인
        assertEquals(theaterName, result.getName());
        assertTrue(theaterRepository.existsById(result.getTheaterId()));

        // 3-2. JDBC로 생성된 좌석 개수 확인 (가장 중요!)
        // 서비스 내부에서 saveAndFlush가 정상 작동했다면, 아래 쿼리로 100개가 조회되어야 함
        Integer seatCount = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM seats WHERE theater_id = ?",
                Integer.class,
                result.getTheaterId()
        );

        assertEquals(rowSize * colSize, seatCount);
    }
}