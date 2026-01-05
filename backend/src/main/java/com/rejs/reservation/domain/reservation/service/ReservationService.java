package com.rejs.reservation.domain.reservation.service;

import com.rejs.reservation.domain.reservation.dto.ReservationDetailDto;
import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSeatNumberDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationSeat;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.ReservationDataFacade;
import com.rejs.reservation.domain.reservation.repository.ReservationQueryRepository;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationSeatRepository;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Observed
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ScreeningRepository screeningRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationQueryRepository reservationQueryRepository;


    // CREATE - Reservation
    @WithSpan("reservation.process.seatLock")
    @Transactional
    public ReservationDto reservationScreening(ReservationRequest request, Long userId){
        // 좌석이 실제로 존재하는 지 가져오기
        // 락이 걸려있는 쿼리임
        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                request.getSeats(), true
        );

        if(!reservationQueryRepository.isScreeningTimeValid(request.getScreeningId())){
            throw new BusinessException(ReservationExceptionCode.SCREENING_TIME_EXPIRED);
        }

        if(availableSeats.size() != request.getSeats().size()){
            throw new BusinessException(ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS);
        }

        // reservation 및 reservationSeat 생성
        Reservation reservation = Reservation.create(userId, request.getScreeningId(), availableSeats);
        reservation = reservationRepository.save(reservation);
        return ReservationDto.from(reservation);
    }

    @WithSpan("reservation.process.screeningLock")
    @Transactional
    public ReservationDto reservationScreeningLock(ReservationRequest request, Long userId){
        // 좌석이 실제로 존재하는 지 가져오기
        // 락이 걸려있는 쿼리임
        Screening screening = screeningRepository.findWithLock(request.getScreeningId());

        if(!screening.getStartTime().isAfter(LocalDateTime.now())){
            throw new BusinessException(ReservationExceptionCode.SCREENING_TIME_EXPIRED);
        }

        List<ScreeningSeat> availableSeats = reservationQueryRepository.selectAvailableSeats(
                request.getSeats(), false
        );

        if(availableSeats.size() != request.getSeats().size()){
            throw new BusinessException(ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS);
        }

        // reservation 및 reservationSeat 생성
        Reservation reservation = Reservation.create(userId, request.getScreeningId(), availableSeats);
        reservation = reservationRepository.save(reservation);
        return ReservationDto.from(reservation);
    }


    // READ
    public Page<ReservationSummaryDto> findMyReservations(long userId, Pageable pageable) {
        return reservationQueryRepository.findMyReservations(userId, pageable);
    }


    public ReservationDetailDto findById(Long id) {
        ReservationSummaryDto reservation = reservationQueryRepository.findById(id);
        List<ReservationSeatNumberDto> seats = reservationQueryRepository.findSeatNumberById(id);
        return new ReservationDetailDto(reservation, seats);
    }
}

