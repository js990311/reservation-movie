package com.rejs.reservation.domain.theater.repository;

import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByTheater(Theater theater);
}
