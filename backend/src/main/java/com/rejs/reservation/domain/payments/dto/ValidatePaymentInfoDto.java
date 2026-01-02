package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import lombok.Getter;

@Getter
public class ValidatePaymentInfoDto {
    private String paymentId;
    private PaymentStatus status;
    private Long reservationId;

    public ValidatePaymentInfoDto(String paymentId, PaymentStatus status, Long reservationId) {
        this.paymentId = paymentId;
        this.status = status;
        this.reservationId = reservationId;
    }

    public static ValidatePaymentInfoDto from(Payment payment){
        return new ValidatePaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), payment.optionalReservationId());
    }
}
