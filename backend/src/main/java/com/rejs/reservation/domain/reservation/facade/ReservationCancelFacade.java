package com.rejs.reservation.domain.reservation.facade;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.facade.PaymentCancelFacade;
import com.rejs.reservation.domain.reservation.service.ReservationCancelService;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Observed
@RequiredArgsConstructor
@Service
public class ReservationCancelFacade {
    private final PaymentCancelFacade paymentCancelFacade;
    private final ReservationCancelService reservationCancelService;

    public void cancelReservation(Long reservationId){
        // 예매를 취소할 때 결제도 같이 취소해야하는 지 검증
        if(!reservationCancelService.checkReservationStatusForCancel(reservationId)){
            // 결제를 취소할 필요가 없으면 바로 cancel상태로 변경
            reservationCancelService.cancelReservation(reservationId);
            return;
        }

        // 취소해야할 결제가 있는 경우
        //      취소할 결제 찾아오기
        String paymentId = reservationCancelService.findForCancelPayment(reservationId);
        try{
            paymentCancelFacade.cancelPayment(paymentId, PaymentCancelReason.CUSTOMER_REQUEST);
        }catch (BusinessException e){
            // 외부 API 실패 시 취소지만 상태 변경이 없었으므로 방치
            throw e;
        }

        // 결제 취소할 필요가 없거나 결제가 취소되었으면 reservation 상태를 취소
        reservationCancelService.cancelReservation(reservationId, paymentId);
    }
}
