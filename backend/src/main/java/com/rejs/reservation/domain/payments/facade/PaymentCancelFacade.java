package com.rejs.reservation.domain.payments.facade;


import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.dto.PaymentCancelDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.service.PaymentCancelCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 결제 검증 실패로 인한 환불을 진행함
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentCancelCrudService paymentCancelCrudService;

    public void cancelPayment(String paymentId, PaymentCancelReason reason){
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
        }catch (PaymentCancelAlreadySuccessException exception){
            // 이미 취소 되엇다
            log.info("[payment.abort.success] 외부 서버에서 이미 취소된 결제. paymentId={}", paymentId);
            paymentCancelCrudService.canceled(paymentCancel.getId());
        }catch (PaymentCancelFailedException exception){
            // 논리상 취소가 불가능하다. 상태를 failed로 변경
            log.warn("[payment.abort.failed] 논리적 오류로 재시도 불가. paymentId={}", paymentId, exception);
            paymentCancelCrudService.failed(paymentCancel.getId());
        } catch (PaymentCancelRetryableException exception){
            // 일시적 예외(네트워크 예외 등) 발생으로 추정
            log.warn("[payment.abort.retry] 일시적 오류로 탈출. paymentId={}", paymentId, exception);
        }catch (Exception e){
            // cancel : 결제 취소가 실패했다. 결제 취소 실패를 해도 상태를 REQUIRED로 두어 다시 취소하도록 지시한다. 중요한 것은 외부 API 서버에서 결제가 취소되지 않았는 데 REQUIRED를 CANCELED로 바꾸는 것이다.
            log.warn("[payment.abort.fail] 예상치 못한 실패로 인한 환불에 실패했습니다. paymentUid={}", paymentId, e);
        }
    }
}
