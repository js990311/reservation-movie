package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PortOnePaymentCancelExceptionCode;
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

import static org.mockito.ArgumentMatchers.*;
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
        requiredDto = new PaymentCancelDto(cancelId, null, paymentId, PaymentCancelStatus.REQUIRED, reason);
    }

    @Test
    @DisplayName("결제 취소 성공 시 상태가 CANCELED 로 변경되어야 한다")
    void abortPayment_Success() {
        // given
        when(paymentCancelCrudService.getOrCreate(anyString(), any())).thenReturn(requiredDto);

        // when
        paymentCancelFacade.cancelPayment(paymentId, reason);

        // then
        verify(portoneAdaptor).cancelPayment(eq(paymentId), eq(reason));
        verify(paymentCancelCrudService).canceled(cancelId);
    }

    @Test
    @DisplayName("이미 취소된 결제라면 결과적으로 CANCELED 처리를 해야 한다")
    void abortPayment_AlreadyCancelled() {
        // given
        when(paymentCancelCrudService.getOrCreate(anyString(), any())).thenReturn(requiredDto);
        doThrow(new PaymentCancelAlreadySuccessException(PortOnePaymentCancelExceptionCode.ALREADY_CANCELLED))
                .when(portoneAdaptor).cancelPayment(paymentId, reason);

        // when
        paymentCancelFacade.cancelPayment(paymentId, reason);

        // then
        verify(paymentCancelCrudService).canceled(cancelId);
    }

    @Test
    @DisplayName("논리적 오류(Failed) 발생 시 failed 메서드가 호출되어야 한다")
    void abortPayment_LogicalFailure() {
        // given
        when(paymentCancelCrudService.getOrCreate(anyString(), any())).thenReturn(requiredDto);
        String errorMsg = "결제 건을 찾을 수 없음";
        doThrow(new PaymentCancelFailedException(PortOnePaymentCancelExceptionCode.NOT_FOUND, errorMsg))
                .when(portoneAdaptor).cancelPayment(paymentId, reason);

        // when
        paymentCancelFacade.cancelPayment(paymentId, reason);

        // then : 상태 Required -> Failed(비즈니스 오류로 인해)
        verify(paymentCancelCrudService).failed(cancelId);
    }

    @Test
    @DisplayName("네트워크 오류 등 재시도 가능 예외 시 DB 상태 변경이 없어야 한다")
    void abortPayment_RetryableFailure() {
        // given
        when(paymentCancelCrudService.getOrCreate(paymentId, reason)).thenReturn(requiredDto);
        doThrow(new PaymentCancelRetryableException(PortOnePaymentCancelExceptionCode.PG_PROVIDER_ERROR, "타임아웃"))
                .when(portoneAdaptor).cancelPayment(paymentId, reason);

        // when
        paymentCancelFacade.cancelPayment(paymentId, reason);

        // then
        verify(paymentCancelCrudService, never()).canceled(cancelId);
        verify(paymentCancelCrudService, never()).failed(cancelId);
    }
}