package com.rejs.reservation.domain.reservation.repository;

import com.rejs.reservation.domain.reservation.dto.ReservationSeatNumberDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationSeatRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ReservationDataFacade {
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ReservationQueryRepository reservationQueryRepository;

    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public Page<ReservationSummaryDto> findMyReservations(Long userId, Pageable pageable) {
        return reservationQueryRepository.findMyReservations(userId, pageable);
    }

    public Reservation findById(Long id){
        return reservationRepository.findById(id).orElseThrow(()-> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
    }

    public Optional<Reservation> findForCancel(Long id){
        return reservationQueryRepository.findForCancel(id);
    }

    public ReservationSummaryDto findReservationSummaryById(Long id) {
        return reservationQueryRepository.findById(id);
    }

    public List<ReservationSeatNumberDto> findSeatNumberById(Long id) {
        return reservationQueryRepository.findSeatNumberById(id);
    }
}
