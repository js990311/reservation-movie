package com.rejs.reservation.domain.payments.service;

public enum PaymentLockResult {
    LOCKED,
    ALREADY_PROCESSING,
    ALREADY_COMPLETED,
    NOT_FOUND
}
