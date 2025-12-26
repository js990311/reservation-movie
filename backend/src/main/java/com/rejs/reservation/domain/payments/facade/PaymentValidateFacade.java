package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentValidateFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentService paymentService;
    private final PaymentAbortFacade paymentAbortFacade;

    public PaymentInfoDto validate(String paymentId){
        PaymentStatusDto payment;
        // 외부 API 호출
        try {
            payment = portoneAdaptor.getPayment(paymentId);
        }catch (Exception e){
            log.error("[payment.validation.api.error] 결제 검증을 위한 외부 통신 실패 paymentId={}", paymentId, e);
            throw e;
        }

        Long reservationId;
        try {
            // 내부 데이터와의 대조
            payment.validate(); // 문제 발생하면 예외 발생

            CustomDataDto customData = payment.getCustomData();
            Long totalAmount = payment.getTotalAmount();
            reservationId = customData.getReservationId();

            // db 내부 데이터와의 검증
            paymentService.validatePayment(paymentId, totalAmount);
            // 성공시 상태 변경
            // Pending -> Confirm
            log.warn("[payment.validation.success] 결제 성공 paymentId={}",paymentId);
            return paymentService.confirmReservation(reservationId, paymentId);
        }catch (BusinessException e){
            /*
             * 실패시 처리
             */
            // 일단 로그를 남겨
            log.warn("[payment.validation.abort] 결제 실패 paymentId={}, error.type={}",paymentId, e.getCode().getType());
            this.handleAbort(paymentId);

            // 3. 예외를 던진다. 
            throw e;
        }catch (RuntimeException e){
            /*
             * 실패시 처리
             */
            // 일단 로그를 남겨
            log.warn("[payment.validation.abort] 결제 실패 paymentId={}",paymentId, e);
            this.handleAbort(paymentId);

            // 3. 예외를 던진다.
            throw e;
        }
    }

    public void handleAbort(String paymentId){
        // 0. reservation의 상태는 변경되지 않는다.

        // 1. 결제의 상태를 READY -> ABORTED로 변경
        paymentService.abortPayment(paymentId);

        // 2. 이미 결제된 payment에 대해서 취소를 진행한다.
        paymentAbortFacade.abortPayment(paymentId);
    }
}
