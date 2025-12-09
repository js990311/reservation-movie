package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.entity.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentLogRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.payment.CancelPaymentResponse;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * 취소를 위한 API 실행을 위해서
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentCancelService {
    private final PaymentClient paymentClient;
    private final PaymentLogRepository paymentLogRepository;

    public void cancelPayment(String paymentId, String reason ){
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
            future.join();
        }catch (Exception e){
            log.error(e.getMessage());
            throw new BusinessException(PaymentExceptionCode.PAYMENT_CANCEL_FAIL, e.getMessage());
        }
    }

    @Transactional
    public void cancelPayment(Long reservationId){
        Optional<PaymentLog> opt = paymentLogRepository.findByReservationIdAndStatus(reservationId, PaymentStatus.PAID);
        if(opt.isEmpty()){
            return;
        }
        PaymentLog paymentLog = opt.get();
        cancelPayment(paymentLog.getPaymentId(),"결제 취소");
        paymentLog.canceled();
    }
}
