package com.rejs.reservation.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;
import java.util.Optional;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.ReservationDataFacade;
import com.rejs.reservation.global.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReservationCancelServiceTest {

    @InjectMocks
    private ReservationCancelService reservationCancelService;

    @Mock
    private PaymentCancelRepository paymentCancelRepository;

    @Mock
    private ReservationDataFacade reservationDataFacade;

    @Test
    @DisplayName("결제 완료된 예매 취소 시, 결제 취소 내역이 저장되고 paymentId가 반환된다.")
    void cancelReservation_WithPaidStatus() {
        // given
        Long reservationId = 1L;
        String paymentUid = "PAY-12345";

        // Mock 객체 생성 및 상태 설정
        Reservation reservation = mock(Reservation.class);
        Payment payment = mock(Payment.class);

        given(payment.getStatus()).willReturn(PaymentStatus.PAID);
        given(payment.getPaymentUid()).willReturn(paymentUid);
        given(reservation.getId()).willReturn(reservationId);
        given(reservation.getPayments()).willReturn(List.of(payment));

        given(reservationDataFacade.findForCancel(reservationId)).willReturn(Optional.of(reservation));

        // when
        Optional<String> result = reservationCancelService.cancelReservation(reservationId);

        // then
        assertThat(result).isPresent().contains(paymentUid);
        verify(paymentCancelRepository, times(1)).save(any(PaymentCancel.class));
        verify(reservation, times(1)).cancel();
    }

    @Test
    @DisplayName("결제 완료된 내역이 없는 예매 취소 시, 예매만 취소되고 빈 값이 반환된다.")
    void cancelReservation_WithoutPaidStatus() {
        // given
        Long reservationId = 1L;
        Reservation reservation = mock(Reservation.class);
        Payment payment = mock(Payment.class);

        given(payment.getStatus()).willReturn(PaymentStatus.READY);
        given(reservation.getPayments()).willReturn(List.of(payment));
        given(reservationDataFacade.findForCancel(reservationId)).willReturn(Optional.of(reservation));

        // when
        Optional<String> result = reservationCancelService.cancelReservation(reservationId);

        // then
        assertThat(result).isEmpty();
        verify(paymentCancelRepository, never()).save(any());
        verify(reservation, times(1)).cancel();
    }

    @Test
    @DisplayName("취소 가능한 에매를 찾지 못한 경우 BusinessException이 발생한다.")
    void cancelReservation_NotFound_ThrowsException() {
        // given
        Long reservationId = 1L;
        given(reservationDataFacade.findForCancel(reservationId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> reservationCancelService.cancelReservation(reservationId))
                .isInstanceOf(BusinessException.class);
    }
}