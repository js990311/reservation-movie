package com.rejs.reservation.domain.reservation.scheduler;

import com.rejs.reservation.domain.reservation.repository.AutoCancelRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationCancelScheduler {
    private final AutoCancelRepository autoCancelRepository;

    @Observed
    @Scheduled(fixedDelay = 20*1000)
    public void autoCancelReservation(){
        long target = autoCancelRepository.autoCancel(200);
    }
}
