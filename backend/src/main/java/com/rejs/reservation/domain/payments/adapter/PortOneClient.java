package com.rejs.reservation.domain.payments.adapter;

import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.payment.CancelPaymentResponse;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@RequiredArgsConstructor
@Component
public class PortOneClient {
    private final PaymentClient paymentClient;

    public Payment getPayment(String paymentId){
        try {
            return paymentClient.getPayment(paymentId).join();
        }catch (CompletionException e){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_API_ERROR);
        }catch (Exception e){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_API_ERROR);
        }
    }

    public CancelPaymentResponse cancelPayment(String paymentId, String reason){
        try {
            CompletableFuture<CancelPaymentResponse> future = paymentClient.cancelPayment(
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
            return future.join();
        }catch (Exception e){
            throw new BusinessException(PaymentExceptionCode.PAYMENT_CANCEL_FAIL, e.getMessage());
        }
    }
}
