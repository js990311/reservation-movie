package com.rejs.reservation.domain.reservation.service;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.ReservationFacade;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReservationCancelService {
    private final PaymentRepository paymentRepository;
    private final PaymentCancelRepository paymentCancelRepository;
    private final ReservationFacade reservationFacade;

    /**
     * 예매 취소할 때 결제도 같이 취소해야하는 지 여부
     * Canceled / PENDING은 이미 결제가 취소되었거나 승인된 결제가 없는 상태이므로 reservation의 상태만 멱등성있게 변경하면 된다.
     * @return true면 결제 취소가 필요하다
     */
    @Transactional(readOnly = true)
    public boolean checkReservationStatusForCancel(Long reservationId) {
        Reservation reservation = reservationFacade.findById(reservationId);
        return reservation.getStatus().equals(ReservationStatus.CONFIRMED);
    }

    /**
     * 취소해야하는 결제 정보를 가져온다.
     * @param reservationId
     * @return
     */
    @Transactional(readOnly = true)
    public String findForCancelPayment(Long reservationId) {
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByReservationIdAndStatus(reservationId, PaymentStatus.PAID).orElseThrow(()-> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));
        return payment.getPaymentUid();
    }

    /**
     * 예매 취소상태를 저장한다
     * @param reservationId
     */
    @Transactional
    public void cancelReservation(Long reservationId){
        Reservation reservation = reservationFacade.findById(reservationId);
        reservation.cancel();
    }

    /**
     * 예매 취소정보를 저장한다.
     * 결제 취소정보도 같이 저장한다.
     * @param reservationId
     * @param paymentId
     */
    @Transactional
    public void cancelReservation(Long reservationId, String paymentId){
        Reservation reservation = reservationFacade.findById(reservationId);
        reservation.cancel();

        PaymentCancel paymentCancel = new PaymentCancel(reservationId, paymentId);
        paymentCancelRepository.save(paymentCancel);

    }

}
