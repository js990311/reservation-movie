package com.rejs.reservation.domain.payments.facade;


import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelSkippedException;
import com.rejs.reservation.domain.payments.dto.PaymentCancelDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.service.PaymentCancelCrudService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentCancelCrudService paymentCancelCrudService;

    public CompletableFuture<Void> cancelPayment(String paymentId){
        log.info("[payment.abort] 결제 취소 처리 시작 paymentId={}", paymentId);

        return CompletableFuture.supplyAsync(
                () -> paymentCancelCrudService.tryLockForCancel(paymentId, PaymentCancelReason.VALIDATION_FAILED)
        ).thenCompose(opt -> {
            // 이미 취소관련 처리가 되어있는 경우
            if(opt.isEmpty()){
                return CompletableFuture.completedFuture(null);
            }

            PaymentCancelDto paymentCancel = opt.get();
            return portoneAdaptor.cancelPayment(paymentId, PaymentCancelReason.VALIDATION_FAILED)
                    .thenAccept(isSuccess-> {
                        if(isSuccess){
                            log.info("[payment.abort.success] 결제 환불 성공 paymentUid={}", paymentId);
                            paymentCancelCrudService.canceled(paymentCancel.getId());
                        }
                    })
                    .exceptionally(
                            e->{
                                Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
                                if (cause instanceof PaymentCancelAlreadySuccessException exception){// 이미 취소 되엇다
                                    log.info("[payment.abort.success] 외부 서버에서 이미 취소된 결제. paymentId={}", paymentId);
                                    paymentCancelCrudService.canceled(paymentCancel.getId());
                                }else if (cause instanceof PaymentCancelFailedException exception) {// 논리상 취소가 불가능하다. 상태를 failed로 변경
                                    log.warn("[payment.abort.failed] 논리적 오류로 재시도 불가. paymentId={}", paymentId, exception);
                                    paymentCancelCrudService.failed(paymentCancel.getId());
                                }else if (cause instanceof PaymentCancelSkippedException exception) {// 논리상 취소가 불가능하다. 상태를 failed로 변경
                                    log.warn("[payment.abort.skipped] 논리적 오류로 재시도 불가. paymentId={}", paymentId, exception);
                                    paymentCancelCrudService.skipped(paymentCancel.getId());
                                } else if(cause instanceof PaymentCancelRetryableException exception){ // 다시 시도하도록 하자
                                    log.warn("[payment.abort.retry] 일시적 오류로 탈출. paymentId={}", paymentId, exception);
                                } else {
                                    log.warn("[payment.abort.unknown] 예상치 못한 실패로 인한 환불에 실패했습니다. paymentUid={}", paymentId, e);
                                }
                                return null;
                            }
                    );
        });
    }
}
