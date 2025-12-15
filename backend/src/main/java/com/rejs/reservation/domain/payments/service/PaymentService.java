package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public void validatePayment(String paymentId, Long totalAmount){
        // readOnly라 멱등성이 어긋날리가 없음
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));
        Reservation reservation = reservationRepository.findById(payment.getReservationId()).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        // 결제 금액이 맞는 지 검증
        if(reservation.getTotalAmount().longValue() != totalAmount){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    @Transactional
    public PaymentInfoDto confirmReservation(Long reservationId, String paymentId){
        Reservation reservation = reservationRepository.findWithLockById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));

        // 이미 상태가 변화되었으면 상태변화 메서드 호출 없이 그대로
        if(reservation.getStatus().equals(ReservationStatus.CONFIRMED) || payment.getStatus().equals(PaymentStatus.PAID)){
            return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
        }
        reservation.confirm();
        payment.paid();
        return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
    }
}
