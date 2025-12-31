package com.rejs.reservation.domain.payments.adapter;

import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.portone.sdk.server.payment.CancelPaymentResponse;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * sdk상 주어지는 client를 호출하기만 하고 그 후처리는 비동기적으로 하도록 할 것
 */
@RequiredArgsConstructor
@Component
public class PortOneClient {
    private final PaymentClient paymentClient;

    @CircuitBreaker(name = "portOne")
    public CompletableFuture<Payment> getPayment(String paymentId){
        return paymentClient.getPayment(paymentId);
    }

    @CircuitBreaker(name = "portOne")
    public CompletableFuture<CancelPaymentResponse> cancelPayment(String paymentId, String reason){
            return paymentClient.cancelPayment(
                    paymentId,
                    null, //amount
                    null, //taxFreeAmount
                    null, // vatAmount
                    reason,
                    null, // requester
                    null, //promotionDiscountRetaionOption
                    null, // currentCancellableAmount
                    null //refundAccount
            );
    }
}
