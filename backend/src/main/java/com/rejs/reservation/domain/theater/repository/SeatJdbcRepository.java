package com.rejs.reservation.domain.theater.repository;

import com.rejs.reservation.domain.theater.entity.Seat;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

@Transactional
@Repository
@RequiredArgsConstructor
public class SeatJdbcRepository {
    private final JdbcTemplate jdbcTemplate;

    private static String INSERT_BATCH_SEATS = """
        INSERT INTO seats (theater_id, row_num, col_num, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())
    """;

    @WithSpan("create.theater.batch")
    public void batchInsertSeats(Long theaterId, int rowSize, int colSize){
        int totalSeats = rowSize * colSize;

        jdbcTemplate.batchUpdate(INSERT_BATCH_SEATS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                int row = (i / colSize) + 1;
                int col = (i % colSize) + 1;

                ps.setLong(1, theaterId);
                ps.setInt(2, row);
                ps.setInt(3, col);
            }

            @Override
            public int getBatchSize() {
                return totalSeats;
            }
        });
    }
}
