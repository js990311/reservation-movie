package com.rejs.reservation.domain.screening.repository;

import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScreeningSeatRepository extends JpaRepository<ScreeningSeat, Long> {
}
