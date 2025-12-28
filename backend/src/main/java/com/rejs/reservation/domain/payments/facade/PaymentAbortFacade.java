package com.rejs.reservation.domain.payments.facade;


import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.dto.PaymentCancelDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.service.PaymentCancelCrudService;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 결제 검증 실패로 인한 환불을 진행함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentAbortFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentCancelCrudService paymentCancelCrudService;

    public void abortPayment(String paymentId){
        log.info("[payment.abort] 결제 취소 처리 시작 paymentId={}", paymentId);
        // try : 결제 취소내역을 의미하는 PaymentCancel을 생성한다.
        PaymentCancelDto paymentCancel = paymentCancelCrudService.getOrCreate(paymentId, PaymentCancelReason.VALIDATION_FAILED);
        if (paymentCancel.isComplete()){
            // 이미 성공하거나 실패함
            // 실패시 로직은 "정산"에서 처리할 예정
            return;
        }

        try {
            // 외부 API 호출
            portoneAdaptor.cancelPayment(paymentId, PaymentCancelReason.VALIDATION_FAILED);

            // confirm : 결제 취소가 성공하였다.
            log.info("[payment.abort.success] 결제 환불 성공 paymentUid={}", paymentId);
            paymentCancelCrudService.canceled(paymentCancel.getId());
        }catch (Exception e){
            // cancel : 결제 취소가 실패했다.
            log.warn("[payment.abort.fail] 예상치 못한 실패로 인한 환불에 실패했습니다. paymentUid={}", paymentId, e);
            paymentCancelCrudService.failed(paymentCancel.getId());
        }
    }
}
