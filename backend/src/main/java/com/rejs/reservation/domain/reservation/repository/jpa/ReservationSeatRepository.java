package com.rejs.reservation.domain.reservation.repository.jpa;

import com.rejs.reservation.domain.reservation.entity.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
}
