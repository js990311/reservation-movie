package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.domain.reservation.service.ReservationService;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentVaildateFacade {
    private final PortOneAdaptor portoneAdaptor;
    private final PaymentService paymentService;

    public PaymentLogDto validate(String paymentId){
        // 외부 API 호출
        PaymentStatusDto payment = portoneAdaptor.getPayment(paymentId);
        Long reservationId;
        try {
            // 내부 데이터와의 대조

            payment.validate(); // 문제 발생하면 예외 발생

            CustomDataDto customData = payment.getCustomData();
            Long totalAmount = payment.getTotalAmount();
            reservationId = customData.getReservationId();

            // db 내부 데이터와의 검증
            paymentService.validatePayment(paymentId, totalAmount);

            // 성공시 상태 변경
            return paymentService.confirmReservation(reservationId, paymentId);
        }catch (BusinessException e){
            // 실패시 결제 취소
            portoneAdaptor.cancelPayment(paymentId, "검증 실패로 인한 결제 취소");
            throw e;
        }
    }
}
