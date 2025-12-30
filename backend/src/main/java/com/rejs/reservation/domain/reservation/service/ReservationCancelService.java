package com.rejs.reservation.domain.reservation.service;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.ReservationDataFacade;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ReservationCancelService {
    private final PaymentCancelRepository paymentCancelRepository;
    private final ReservationDataFacade reservationDataFacade;

    /**
     * 예매 취소할 때 결제도 같이 취소해야하는 지 여부
     * Canceled / PENDING은 이미 결제가 취소되었거나 승인된 결제가 없는 상태이므로 reservation의 상태만 멱등성있게 변경하면 된다.
     * @return 결제 취소할 paymentId
     */
    @Transactional
    public Optional<String> cancelReservation(Long reservationId) {
        /*
            쿼리 조건은 다음과 같음
            1. 이미 취소된 상태가 아닐 것
            2. 상영시작시간 전일 것
            데이터베이스에서 찾아오지 못했다면 취소 불가능한 것
         */
        Reservation reservation = reservationDataFacade.findForCancel(reservationId).orElseThrow(() -> new BusinessException(ReservationExceptionCode.INVALID_RESERVATION_CANCEL_REQUEST));

        // 결제 완료된 paymentId가 있는지 검사
        String paymentId = null;
        Optional<Payment> opt = reservation.getPayments().stream().filter((payment -> payment.getStatus().equals(PaymentStatus.PAID))).findFirst();
        if(opt.isPresent()){
            // 있으면 paymentCancel을 생성하고 paymentId를 위로 올림
            paymentId = opt.get().getPaymentUid();
            PaymentCancel paymentCancel = new PaymentCancel(opt.get(), PaymentCancelReason.CUSTOMER_REQUEST);
            paymentCancelRepository.save(paymentCancel);
        }

        // 이제 취소
        reservation.cancel();

        // 마지막으로 취소 paymentId 올리기
        return Optional.ofNullable(paymentId);
    }
}
