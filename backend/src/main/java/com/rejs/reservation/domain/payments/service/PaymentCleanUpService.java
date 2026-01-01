package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.facade.PaymentCancelFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class PaymentCleanUpService {
    private final PaymentService paymentService;
    private final PaymentCancelFacade paymentCancelFacade;

    public CompletableFuture<Void> cleanUp(String paymentUid){
        return CompletableFuture.supplyAsync(
                ()->paymentService.processZombiePayment(paymentUid)
        ).thenAcceptAsync(isZombie->{
            if(isZombie){
                paymentCancelFacade.cancelPayment(paymentUid);
            }
        });
    }
}
