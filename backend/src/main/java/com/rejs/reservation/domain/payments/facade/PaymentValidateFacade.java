package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.service.PaymentLockResult;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentValidateFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentService paymentService;
    private final PaymentCancelFacade paymentCancelFacade;

    public PaymentInfoDto validate(String paymentId){

        // 1. 멱등성 보장을 위한 로직 및 결제가 성공했다는 의미를 위해 Ready -> Verifying
        log.info("[payment.verify.request] 결제 검증 요청이 들어왔습니다. paymentId={}", paymentId);
        try {
            PaymentLockResult paymentLockResult = paymentService.startVerification(paymentId);
            if(paymentLockResult.equals(PaymentLockResult.ALREADY_COMPLETED)){
                // 이미 처리된 검증이거나, 검증 중
                log.info("[payment.verify.already] 이미 검증되었거나 처리중입니다. paymentId={}", paymentId);
                return paymentService.getPaymentInfo(paymentId);
            }else if(paymentLockResult.equals(PaymentLockResult.NOT_FOUND)){
                // 서버에서 결제하라고 지시하지 않은 결제를 시도함 -> 환불
                log.warn("[payment.unknown] 서버와 합의되지 않은 결제가 이루어졌습니다. paymentId={}", paymentId);
                handleAbort(paymentId);
                return paymentService.getPaymentInfo(paymentId);
            }
        }catch (CannotAcquireLockException ex){
            log.info("[payment.verify.fail] 동시에 다른 누군가 처리 중입니다. paymentId={}", paymentId, ex);
            return paymentService.getPaymentInfo(paymentId);
        }

        // 2. 외부 API 호출
        PaymentStatusDto payment;
        try {
            payment = portoneAdaptor.getPayment(paymentId).join();
        }catch (Exception e){
            log.error("[payment.validation.api.error] 결제 검증을 위한 외부 통신 실패 paymentId={}", paymentId, e);
            // VERIFYING에서 상태를 바꾸지 않는 이유는 나중에 스케쥴러 등을 통해서 재시도를 할 것이기 때문. 즉 검증이 끝나야 다음 상태로 넘어갈 수 있다
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
            // 성공시 상태 변경
            // Pending -> Confirm
            return paymentService.validateAndConfirm(reservationId, paymentId, totalAmount);
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
        paymentCancelFacade.cancelPayment(paymentId);
    }
}
