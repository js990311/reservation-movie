package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.ValidatePaymentInfoDto;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletionException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentValidateFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentService paymentService;
    private final PaymentCancelFacade paymentCancelFacade;

    public ValidatePaymentInfoDto validate(String paymentId){
        try {
            return tryConfirm(paymentId);
        } catch (BusinessException e) {
            log.warn("[payment.verify.fail] 검증 실패(Business). 즉시 취소. id={}, msg={}", paymentId, e.getMessage());
            cancel(paymentId);
            throw e;
        } catch (CompletionException e) {
            log.error("[payment.verify.fail] 외부 API 호출 중 에러. 즉시 취소. id={}", paymentId, e);
            cancel(paymentId);
            if (e.getCause() instanceof BusinessException bizEx) {
                throw bizEx;
            }
            throw e;
        } catch (Exception e) {
            log.error("[payment.verify] 알 수 없는 에러. 즉시 취소. id={}", paymentId, e);
            cancel(paymentId);
            throw e;
        }
    }

    public void cancel(String paymentId){
        try {
            // 0. reservation의 상태는 변경되지 않는다.
            // 1. 결제의 상태를 READY -> ABORTED로 변경
            paymentService.abortPayment(paymentId);

            // 2. 이미 결제된 payment에 대해서 취소를 진행한다.
            paymentCancelFacade.cancelPayment(paymentId);
        }catch (Exception e){
            log.error("[ACTION_REQUIRED] [payment.verify.cancel.fail] 보상트랜잭션 실패", e);
            throw e;
        }
    }

    public ValidatePaymentInfoDto tryConfirm(String paymentId){
        // 결제가 존재하는지. 누군가 처리했는지 문의
        if(!paymentService.tryLockForVerification(paymentId)){
            // 이미 처리된 검증이거나, 검증 중
            log.info("[payment.verify.already] 이미 검증되었거나 처리중입니다. paymentId={}", paymentId);
            return paymentService.getPaymentInfo(paymentId);
        }

        PaymentStatusDto payment = portoneAdaptor.getPayment(paymentId).join();
        payment.validate(); // 문제 발생하면 예외 발생

        CustomDataDto customData = payment.getCustomData();
        Long totalAmount = payment.getTotalAmount();
        Long reservationId = customData.getReservationId();

        // db 내부 데이터와의 검증
        // 성공시 상태 변경
        // Pending -> Confirm
        return paymentService.validateAndConfirm(reservationId, paymentId, totalAmount);
    }
}
