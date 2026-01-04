package com.rejs.reservation.domain.payments.scheduler;

import com.rejs.reservation.domain.payments.facade.PaymentCancelFacade;
import com.rejs.reservation.domain.payments.facade.PaymentValidateFacade;
import com.rejs.reservation.domain.payments.repository.PaymentCancelQueryRepository;
import com.rejs.reservation.domain.payments.repository.PaymentQueryRepository;
import com.rejs.reservation.domain.payments.service.PaymentCleanUpService;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
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
public class PaymentScheduler {
    private final PaymentCancelQueryRepository paymentCancelQueryRepository;
    private final PaymentQueryRepository paymentQueryRepository;
    private final PaymentCancelFacade paymentCancelFacade;
    private final PaymentCleanUpService paymentCleanUpService;

    @WithSpan("scheduler.payment.cancel")
    @Scheduled(fixedDelay = 5*60*1000) // 이전 작업으로부터 5분마다 실행
    public void autoCancelPayment(){
        List<String> paymentIds = paymentCancelQueryRepository.findAbandonedPaymentCancels(LocalDateTime.now().minusMinutes(5), 10);
        log.debug("[scheduler.payment.cancel] cancel = {}", paymentIds.size());
        CompletableFuture.allOf(
                paymentIds.stream().map(
                        paymentCancelFacade::cancelPayment
                ).toArray(CompletableFuture[]::new)
        ).join();
    }

    @Observed(name = "scheduler.payment.cleanup")
    @Scheduled(fixedDelay = 5*60*1000) // 이전 작업으로부터 5분마다 실행
    public void cleanUpPayment(){
        List<String> zombiePaymentUid = paymentQueryRepository.findZombiePayment(LocalDateTime.now().minusMinutes(5), 10);
        log.debug("[scheduler.payment.cleanup] cancel = {}", zombiePaymentUid.size());
        CompletableFuture.allOf(
                zombiePaymentUid.stream().map(
                        paymentCleanUpService::cleanUp
                ).toArray(CompletableFuture[]::new)
        ).join();

    }

}
