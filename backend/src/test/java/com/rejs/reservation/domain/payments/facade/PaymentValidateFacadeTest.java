package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class PaymentValidateFacadeTest {
    @Mock
    private PortOneAdaptor portOneAdaptor;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentAbortFacade paymentAbortFacade;

    @InjectMocks
    private PaymentValidateFacade paymentValidateFacade;

    @Test
    @DisplayName("결제 성공시나리오")
    void validate() {
        String paymentId = "123456";
        Long amount = 1000L;
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(1L);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // 모든 검증이 성공했는데 트랜잭션에서 실패
        paymentValidateFacade.validate(paymentId);

        // w
        verify(paymentService, times(1)).confirmReservation(1L,paymentId);

        // 실패로직
        verify(paymentService, never()).abortPayment(anyString());
        verify(paymentAbortFacade, never()).abortPayment(anyString());
    }

    @Test
    @DisplayName("외부 API 실패 시나리오")
    void validateGetPaymentInfoFail(){
        String paymentId = "123456";

        // 외부 API가 비정상적으로 작동 = 결제 시도 정보 획득에 실패
        when(portOneAdaptor.getPayment(paymentId)).thenThrow(BusinessException.of(PaymentExceptionCode.PAYMENT_API_ERROR));

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 성공로직 실행 X
        verify(paymentService, never()).confirmReservation(anyLong(),anyString());

        // 실패로직
        verify(paymentService, never()).abortPayment(anyString());
        verify(paymentAbortFacade, never()).abortPayment(anyString());
    }

    @Test
    @DisplayName("결제 메타데이터 실패 시나리오")
    void validatePaymentStatusValidationFail(){
        String paymentId = "123456";
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);

        // paymentStatus에서의 검증 실패 시나리오
        doThrow(BusinessException.of(PaymentExceptionCode.INVALID_CHANNEL)).when(paymentStatus).validate();

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 성공로직 실행 X
        verify(paymentService, never()).confirmReservation(anyLong(),anyString());

        // 실패로직
        verify(paymentService, times(1)).abortPayment(anyString());
        verify(paymentAbortFacade, times(1)).abortPayment(anyString());
    }

    @Test
    @DisplayName("결제 금액 검증 실패 시나리오")
    void validatePaymentFail(){
        String paymentId = "123456";
        Long amount = 1000L;
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(1L);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // paymentService에서의 검증 실패 시나리오
        doThrow(BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH)).when(paymentService).validatePayment(paymentId, amount);

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 성공로직 실행 X
        verify(paymentService, never()).confirmReservation(anyLong(),anyString());

        // 실패로직
        verify(paymentService, times(1)).abortPayment(anyString());
        verify(paymentAbortFacade, times(1)).abortPayment(anyString());
    }

    @Test
    @DisplayName("결제 승인 실패 시나리오")
    void validateConfirmFail(){
        String paymentId = "123456";
        Long amount = 1000L;
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(1L);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // 모든 검증이 성공했는데 트랜잭션에서 실패
        when(paymentService.confirmReservation(customDataDto.getReservationId(), paymentId)).thenThrow(new RuntimeException());

        // w
        assertThrows(Exception.class,()->paymentValidateFacade.validate(paymentId));

        // 실패로직
        verify(paymentService, times(1)).abortPayment(anyString());
        verify(paymentAbortFacade, times(1)).abortPayment(anyString());
    }

}
