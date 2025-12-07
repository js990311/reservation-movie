package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.entity.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentLogDto {
    private String paymentId;
    private PaymentStatus status;
    private Long reservationId;

    public PaymentLogDto(String paymentId, PaymentStatus status, Long reservationId) {
        this.paymentId = paymentId;
        this.status = status;
        this.reservationId = reservationId;
    }

    public static PaymentLogDto from(PaymentLog paymentLog){
        return new PaymentLogDto(
                paymentLog.getPaymentId(),
                paymentLog.getStatus(),
                paymentLog.getReservationId()
        );
    }
}
