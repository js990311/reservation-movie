package com.rejs.reservation.domain.reservation.scheduler;

import com.rejs.reservation.domain.reservation.repository.AutoCancelRepository;
import com.rejs.reservation.domain.reservation.repository.ReservationDataFacade;
import com.rejs.reservation.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationCancelScheduler {
    private final AutoCancelRepository autoCancelRepository;

    @Scheduled(fixedDelay = 60*1000) // 1분마다 실행
    public void autoCancelReservation(){
        // 트랜잭션은 repository에서 각각 실행됨
        autoCancelRepository.autoCancelByCreatedAt();
        autoCancelRepository.autoCancelByScreeningStartTime();
    }
}
