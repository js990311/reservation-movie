package com.rejs.reservation.domain.payments.scheduler;

import com.rejs.reservation.domain.payments.facade.PaymentCancelFacade;
import com.rejs.reservation.domain.payments.repository.PaymentCancelQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentCancelScheduler {
    private final PaymentCancelQueryRepository paymentCancelQueryRepository;
    private final PaymentCancelFacade paymentCancelFacade;

    @Scheduled(fixedDelay = 60*1000) // 1분마다 실행
    public void autoCancelReservation(){
        List<String> paymentIds = paymentCancelQueryRepository.findAbandonedPaymentCancels(LocalDateTime.now().minusMinutes(3), 10);
        CompletableFuture.allOf(
                paymentIds.stream().map(
                        paymentCancelFacade::cancelPayment
                ).toArray(CompletableFuture[]::new)
        ).join();
    }
}
