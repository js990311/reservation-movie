package com.rejs.reservation.domain.reservation.repository.jpa;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
