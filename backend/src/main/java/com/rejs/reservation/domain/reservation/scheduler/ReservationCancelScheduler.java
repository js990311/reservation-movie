package com.rejs.reservation.domain.reservation.scheduler;

import com.rejs.reservation.domain.reservation.repository.ReservationDataFacade;
import com.rejs.reservation.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationCancelScheduler {
    private final ReservationService reservationService;
    private final ReservationDataFacade reservationDataFacade;

    @Scheduled(fixedDelay = 60*1000)
    public void autoCancelReservation(){
        reservationDataFacade.autoCancelReservation();
    }
}
