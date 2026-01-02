package com.rejs.reservation.domain.reservation.repository.jpa;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Reservation> findWithLockById(Long id);

    @Query(nativeQuery = true, value = """
                SELECT reservation_id
                FROM reservations r
                JOIN screenings s using(screening_id)
                where status = 'PENDING'
                    AND (r.created_at < :threshold OR s.start_time < :now)
                LIMIT :limit
                FOR UPDATE SKIP LOCKED;
    """)
    List<Long> autoCancelReservation(
            @Param("now")LocalDateTime now,
            @Param("threshold")LocalDateTime threshold,
            @Param("limit") int limit
    );

    @Query("SELECT s.startTime FROM Screening s JOIN Reservation r ON r.screeningId = s.id WHERE r.id = :reservationId")
    Optional<LocalDateTime> findStartTimeByReservationId(@Param("reservationId") Long reservationId);
}
