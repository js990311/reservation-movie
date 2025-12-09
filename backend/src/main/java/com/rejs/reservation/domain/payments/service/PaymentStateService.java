package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentLogRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class PaymentStateService {
    private final PaymentLogRepository paymentLogRepository;
    private final ReservationRepository reservationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentLog successPaymentLog(String paymentId, Long reservationId){
        PaymentLog paymentLog = paymentLogRepository.findByPaymentId(paymentId).orElseGet(() -> paymentLogRepository.save(new PaymentLog(paymentId)));
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()->new BusinessException(PaymentExceptionCode.RESERVATION_NOT_FOUND));
        paymentLog.mapReservaiton(reservation);
        paymentLog.success();
        reservation.confirm();
        return paymentLog;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public PaymentLog failPaymentLog(String paymentId, Long reservationId, String reason){
        PaymentLog paymentLog = paymentLogRepository.findByPaymentId(paymentId).orElseGet(() -> paymentLogRepository.save(new PaymentLog(paymentId)));
        if(reservationId != null){
            reservationRepository.findById(reservationId).ifPresent(paymentLog::mapReservaiton);
        }
        paymentLog.failed();
        return paymentLog;
    }
}
