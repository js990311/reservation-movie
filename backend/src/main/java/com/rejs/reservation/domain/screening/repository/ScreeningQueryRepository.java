package com.rejs.reservation.domain.screening.repository;

import com.rejs.reservation.domain.theater.entity.Theater;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class ScreeningQueryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final static String EXISTS_QUERY = """
                SELECT 1 
                FROM screenings scr 
                WHERE theater_id = ? AND   
                    (? < end_time ) AND 
                    (start_time < ?) 
                LIMIT 1; 
            """;

    public boolean existsByScreeningTime(Long theaterId, LocalDateTime startTime, LocalDateTime endTime){
        List<Integer> i = jdbcTemplate.queryForList(EXISTS_QUERY, Integer.class, theaterId, startTime, endTime);
        return !i.isEmpty();
    }
}
