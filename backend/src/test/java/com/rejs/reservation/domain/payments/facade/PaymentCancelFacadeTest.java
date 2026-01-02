package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.exception.cancel.PortOnePaymentCancelExceptionCode;
import com.rejs.reservation.domain.payments.dto.PaymentCancelDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.service.PaymentCancelCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCancelFacadeTest {

    @Mock
    private PortOneAdaptor portoneAdaptor;

    @Mock
    private PaymentCancelCrudService paymentCancelCrudService;

    @InjectMocks
    private PaymentCancelFacade paymentCancelFacade;

    private final String paymentId = "test_payment_123";
    private final Long cancelId = 1L;
    private PaymentCancelDto requiredDto;
    private PaymentCancelReason reason = PaymentCancelReason.VALIDATION_FAILED;

    @BeforeEach
    void setUp() {
        requiredDto = new PaymentCancelDto(cancelId, paymentId, PaymentCancelStatus.REQUIRED, reason);
    }

    @Test
    @DisplayName("결제 취소 성공 시 상태가 CANCELED 로 변경되어야 한다")
    void abortPayment_Success() {
        // given
        when(paymentCancelCrudService.tryLockForCancel(paymentId, reason)).thenReturn(Optional.of(requiredDto));
        when(portoneAdaptor.cancelPayment(paymentId, reason)).thenReturn(CompletableFuture.completedFuture(true));

        // when
        paymentCancelFacade.cancelPayment(paymentId).join();

        // then
        verify(paymentCancelCrudService, times(1)).canceled(cancelId);
    }

    @Test
    @DisplayName("이미 취소된 결제라면 결과적으로 CANCELED 처리를 해야 한다")
    void abortPayment_AlreadyCancelled() {
        // given
        when(paymentCancelCrudService.tryLockForCancel(paymentId, reason)).thenReturn(Optional.of(requiredDto));
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new PaymentCancelAlreadySuccessException(PortOnePaymentCancelExceptionCode.ALREADY_CANCELLED));
        when(portoneAdaptor.cancelPayment(paymentId, reason)).thenReturn(failedFuture);

        // when
        paymentCancelFacade.cancelPayment(paymentId).join();

        // then
        verify(paymentCancelCrudService, times(1)).canceled(cancelId);
    }

    @Test
    @DisplayName("논리적 오류(Failed) 발생 시 failed 메서드가 호출되어야 한다")
    void abortPayment_LogicalFailure() {
        // given
        when(paymentCancelCrudService.tryLockForCancel(paymentId, reason)).thenReturn(Optional.of(requiredDto));
        String errorMsg = "결제 건을 찾을 수 없음";
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new PaymentCancelFailedException(PortOnePaymentCancelExceptionCode.NOT_FOUND, errorMsg));
        when(portoneAdaptor.cancelPayment(paymentId, reason)).thenReturn(failedFuture);

        // when
        paymentCancelFacade.cancelPayment(paymentId).join();

        // then : 상태 Required -> Failed(비즈니스 오류로 인해)
        verify(paymentCancelCrudService, never()).canceled(cancelId);
        verify(paymentCancelCrudService, times(1)).failed(cancelId);
    }

    @Test
    @DisplayName("네트워크 오류 등 재시도 가능 예외 시 DB 상태 변경이 없어야 한다")
    void abortPayment_RetryableFailure() {
        // given
        when(paymentCancelCrudService.tryLockForCancel(paymentId, reason)).thenReturn(Optional.of(requiredDto));
        CompletableFuture<Boolean> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new PaymentCancelRetryableException(PortOnePaymentCancelExceptionCode.PG_PROVIDER_ERROR, "타임아웃"));
        when(portoneAdaptor.cancelPayment(paymentId, reason)).thenReturn(failedFuture);

        // when
        paymentCancelFacade.cancelPayment(paymentId).join();

        // then
        verify(paymentCancelCrudService, never()).canceled(cancelId);
        verify(paymentCancelCrudService, never()).failed(cancelId);
    }
}