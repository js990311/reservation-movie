package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentPrepareDto;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
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
public class PaymentPrepareService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    /**
     * paymentId와 customData를 생성하고 결제전 사전 검증을 수행하는 method
     * @param reservationId 결제를 진행할 reservation
     * @return
     */
    @Transactional
    public PaymentPrepareDto prepare(long reservationId) {
        // payments를 준비상태로 만들고 paymentId를 생성한다.
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));

        if (!reservation.getStatus().equals(ReservationStatus.PENDING)){
            // 취소되었거나 결제가 완료되었다면 결제를 수행하지 않음
            throw BusinessException.of(PaymentExceptionCode.ALREADY_PAID_RESERVATION);
        }

        // 결제 시도를 ready 상태로 생성함
        Payment payment = Payment.create(reservation);
        CustomDataDto customDataDto = new CustomDataDto(reservation.getId());
        return new PaymentPrepareDto(payment.getPaymentUid(), reservation.getTotalAmount(),customDataDto);
    }
}
