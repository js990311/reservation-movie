package com.rejs.reservation.domain.payments.entity.cancel;

public enum PaymentCancelStatus {
    REQUIRED, // 환불이 필요한 경우 
    CANCELED, // 환불이 완료된 경우 
    FAILED, // 환불이 불가능한 경우
    SKIPPED // 환불이 필요없는 경우 
    ;
}
