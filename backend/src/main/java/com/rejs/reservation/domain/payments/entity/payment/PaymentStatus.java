package com.rejs.reservation.domain.payments.entity.payment;

public enum PaymentStatus {
    READY,
    VERIFYING,
    PAID,
    ABORTED,
    TIMEOUT
}
