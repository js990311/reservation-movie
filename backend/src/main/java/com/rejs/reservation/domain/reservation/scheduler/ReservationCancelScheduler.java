package com.rejs.reservation.domain.reservation.scheduler;

import com.rejs.reservation.domain.reservation.repository.AutoCancelRepository;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ReservationCancelScheduler {
    private final AutoCancelRepository autoCancelRepository;

    @WithSpan("scheduler.reservation.cancel")
    @Scheduled(fixedDelay = 60*1000)
    public void autoCancelReservation(){
        long target = autoCancelRepository.autoCancel(200);
        log.debug("[scheduler.reservation.cancel] clean target = {}", target);
    }
}
