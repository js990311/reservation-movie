package com.rejs.reservation.domain.payments.dto;

import lombok.Getter;

@Getter
public class PaymentPrepareDto {
    private String paymentId;
    private Integer totalAmount;
    private CustomDataDto customData;

    public PaymentPrepareDto(String paymentId, Integer totalAmount, CustomDataDto customData) {
        this.paymentId = paymentId;
        this.totalAmount = totalAmount;
        this.customData = customData;
    }
}
