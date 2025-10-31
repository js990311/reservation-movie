package com.rejs.reservation.domain.screening.repository;

import com.rejs.reservation.domain.screening.entity.Screening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ScreeningRepository extends JpaRepository<Screening, Long> {

    @Query("""
        SELECT COUNT(sc)>0
        FROM Screening sc
        WHERE 
            sc.theater.id = :theaterId
            AND sc.startTime < :endTime
            AND sc.endTime > :startTime
   """)
    boolean existsByScreeningTime(@Param("theaterId") Long theaterId, @Param("startTime")LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
