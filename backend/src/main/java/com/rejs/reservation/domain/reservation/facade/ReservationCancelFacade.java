package com.rejs.reservation.domain.reservation.facade;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.facade.PaymentCancelFacade;
import com.rejs.reservation.domain.reservation.service.ReservationCancelService;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Observed
@RequiredArgsConstructor
@Service
public class ReservationCancelFacade {
    private final PaymentCancelFacade paymentCancelFacade;
    private final ReservationCancelService reservationCancelService;

    public void cancelReservation(Long reservationId){
        // 예매 취소 및 결제 취소 검증
        Optional<String> paymentUid = reservationCancelService.cancelReservation(reservationId);

        // 결제 취소할 필요가 있으면 환불 호출
        paymentUid.ifPresent(paymentCancelFacade::cancelPayment);

        // 종료
    }
}
