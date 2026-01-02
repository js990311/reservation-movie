package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PaymentCancelInfo {
    private PaymentCancelStatus cancelStatus;
    private LocalDateTime canceledAt;
    private String cancelReason;

    public PaymentCancelInfo(PaymentCancelStatus cancelStatus, LocalDateTime canceledAt, String cancelReason) {
        this.cancelStatus = cancelStatus;
        this.canceledAt = canceledAt;
        this.cancelReason = cancelReason;
    }
}
