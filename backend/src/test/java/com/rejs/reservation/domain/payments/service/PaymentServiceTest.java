package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @InjectMocks
    private PaymentService paymentService; // 테스트 대상

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentCancelRepository paymentCancelRepository;

    @Mock private Payment payment;
    @Mock private Reservation reservation;

    @Test
    @DisplayName("Lock 획득 실패: 업데이트된 Row가 0개면 false를 반환한다")
    void processZombiePayment() {
        // given
        String paymentUid = "test-uid";
        given(paymentRepository.updateForCleanUp(eq(paymentUid), any(), any()))
                .willReturn(0); // 업데이트 0건

        // when
        boolean result = paymentService.processZombiePayment(paymentUid);

        // then
        assertFalse(result);
        // 리포지토리 조회가 호출되지 않아야 함
        verify(paymentRepository, never()).findByPaymentUid(anyString());
    }

    @Test
    @DisplayName("동시성 이슈 등으로 결제 정보가 조회되지 않을 경우: Not Found 처리 및 취소 생성 후 true 반환")
    void processZombiePayment_notFound() {
        // given
        String paymentUid = "ghost-uid";
        given(paymentRepository.updateForCleanUp(eq(paymentUid), any(), any()))
                .willReturn(1); // Lock 획득 성공
        given(paymentRepository.findByPaymentUid(paymentUid))
                .willReturn(Optional.empty()); // 그러나 조회 실패

        // when
        boolean result = paymentService.processZombiePayment(paymentUid);

        // then
        assertTrue(result);
        verify(paymentRepository).saveAndFlush(any(Payment.class)); // 새 결제정보 저장
        verify(paymentCancelRepository).save(any(PaymentCancel.class)); // 취소 내역 저장
    }

    @Test
    @DisplayName("상태 READY: 예매가 PENDING이 아니라면(이미 확정/취소됨) -> Timeout 처리 후 true")
    void processZombiePayment_ready_timeout() {
        // given
        String paymentUid = "ready-uid";

        // Mocking: Payment -> READY, Reservation -> CONFIRMED (Not Pending)
        given(paymentRepository.updateForCleanUp(eq(paymentUid), any(), any())).willReturn(1);
        given(paymentRepository.findByPaymentUid(paymentUid)).willReturn(Optional.of(payment));

        given(payment.getStatus()).willReturn(PaymentStatus.READY);
        given(payment.getReservation()).willReturn(reservation);
        given(reservation.getStatus()).willReturn(ReservationStatus.CONFIRMED); // !PENDING

        // when
        boolean result = paymentService.processZombiePayment(paymentUid);

        // then
        assertTrue(result);
        verify(payment).timeout(); // 타임아웃 메서드 호출 검증
        verify(paymentCancelRepository).save(any(PaymentCancel.class));
    }

    @Test
    @DisplayName("상태 READY: 예매가 정상적으로 PENDING 상태라면 -> 아무 작업 안함(false)")
    void processZombiePayment_ready_pending() {
        // given
        String paymentUid = "normal-uid";

        given(paymentRepository.updateForCleanUp(eq(paymentUid), any(), any())).willReturn(1);
        given(paymentRepository.findByPaymentUid(paymentUid)).willReturn(Optional.of(payment));

        given(payment.getStatus()).willReturn(PaymentStatus.READY);
        given(payment.getReservation()).willReturn(reservation);
        given(reservation.getStatus()).willReturn(ReservationStatus.PENDING);

        // when
        boolean result = paymentService.processZombiePayment(paymentUid);

        // then
        assertFalse(result);
        verify(payment, never()).timeout();
        verify(paymentCancelRepository, never()).save(any());
    }

    @Test
    @DisplayName("상태 VERIFYING: 검증 실패로 간주 -> Aborted 처리 후 true")
    void processZombiePayment_verifying_abort() {
        // given
        String paymentUid = "verify-uid";

        given(paymentRepository.updateForCleanUp(eq(paymentUid), any(), any())).willReturn(1);
        given(paymentRepository.findByPaymentUid(paymentUid)).willReturn(Optional.of(payment));

        given(payment.getStatus()).willReturn(PaymentStatus.VERIFYING);
        // VERIFYING 상태는 Reservation 체크 로직 없이 바로 Aborted

        // when
        boolean result = paymentService.processZombiePayment(paymentUid);

        // then
        assertTrue(result);
        verify(payment).aborted(); // 중단 메서드 호출 검증
        verify(paymentCancelRepository).save(any(PaymentCancel.class));
    }
}