package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentInfo {
    private String paymentUid;
    private PaymentStatus paymentStatus;
    private Long reservationId;
    private LocalDateTime createAt;
    private PaymentCancelInfo cancelInfo;

    public PaymentInfo(String paymentUid, PaymentStatus paymentStatus, Long reservationId, LocalDateTime createAt, PaymentCancelInfo cancelInfo) {
        this.paymentUid = paymentUid;
        this.paymentStatus = paymentStatus;
        this.reservationId = reservationId;
        this.createAt = createAt;
        this.cancelInfo = cancelInfo;
    }
}
