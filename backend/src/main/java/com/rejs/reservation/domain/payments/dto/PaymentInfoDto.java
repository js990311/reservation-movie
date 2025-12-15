package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentInfoDto {
    private String paymentId;
    private PaymentStatus status;
    private Long reservationId;

    public PaymentInfoDto(String paymentId, PaymentStatus status, Long reservationId) {
        this.paymentId = paymentId;
        this.status = status;
        this.reservationId = reservationId;
    }
}
