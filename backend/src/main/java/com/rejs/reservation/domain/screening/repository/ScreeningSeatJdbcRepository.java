package com.rejs.reservation.domain.screening.repository;

import com.rejs.reservation.domain.screening.entity.Screening;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ScreeningSeatJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    String batchInsertScreeningSeat = """
                    INSERT INTO screening_seats (screening_id, seat_id, status, created_at, updated_at, price)  
                    SELECT ?, s.seat_id, 'AVAILABLE', NOW(), NOW(), 10000 
                    FROM seats s
                    WHERE s.theater_id = ? AND s.deleted_at IS NULL
                """;

    @Transactional
    public void batchInsertScreeningSeat(Long screeningId, Long theaterId){
        jdbcTemplate.update(batchInsertScreeningSeat, screeningId, theaterId);
    }
}
