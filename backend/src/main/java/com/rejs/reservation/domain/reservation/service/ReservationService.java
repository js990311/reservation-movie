package com.rejs.reservation.domain.reservation.service;

import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.repository.ReservationFacade;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationFacade reservationFacade;
    private final ScreeningRepository screeningRepository;

    // CREATE - Reservation
    @Transactional
    public void reservationScreening(ReservationRequest request){
        // 비관적 락
        Screening screening = screeningRepository.findWithLock(request.getScreeningId());

        // 좌석이 실제로 존재하는 지 가져오기
        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                request.getSeats(),
                screening.getId(),
                screening.getTheaterId()
        );

        // 이미 예약된 좌석인지 검사하기

        // reservation 및 reservationSeat 생성
    }
}

