package com.rejs.reservation.domain.reservation.repository;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ReservationFacade {
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    private static String FIND_AVAILABLE_SEAT = """
                SELECT s.seat_id
                FROM seats s 
                WHERE 
                    s.seat_id in (:seatIds)
                    AND s.theater_id = :theaterId
                    AND NOT EXISTS(
                        SELECT 1 
                        FROM reservation_seats rs 
                        JOIN reservations r using (reservation_id)
                        WHERE rs.seat_id = s.seat_id 
                            AND r.screening_id = :screeningId 
                    )
                ; 
            """;

    public List<Long> selectAvailableSeats(List<Long> seatIds, Long theaterId, Long screeningId){
        MapSqlParameterSource param = new MapSqlParameterSource()
                .addValue("seatIds", seatIds)
                .addValue("theaterId", theaterId)
                .addValue("screeningId", screeningId)
        ;
        return jdbcTemplate.queryForList(FIND_AVAILABLE_SEAT, param, Long.class);
    }

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}
