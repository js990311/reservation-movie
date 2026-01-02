package com.rejs.reservation.domain.reservation.facade;

import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationFacade {
    private final ReservationService reservationService;

    public ReservationDto reservationScreening(ReservationRequest request, Long userId){
        try {
            ReservationDto reservationDto = reservationService.reservationScreening(request, userId);
            return reservationDto;
        }catch (CannotAcquireLockException ex){
            log.warn("DEADLOCK!!!");
            throw ex;
        }

    }

}
