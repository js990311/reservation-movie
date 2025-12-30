package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import lombok.Getter;

@Getter
public class PaymentCancelDto {
    private Long id;
    private String paymentUid;
    private PaymentCancelStatus status;
    private PaymentCancelReason reason;

    public boolean isComplete(){
        return !status.equals(PaymentCancelStatus.REQUIRED);
    }

    public PaymentCancelDto(Long id, String paymentUid, PaymentCancelStatus status, PaymentCancelReason reason) {
        this.id = id;
        this.paymentUid = paymentUid;
        this.status = status;
        this.reason = reason;
    }

    public static PaymentCancelDto from(PaymentCancel paymentCancel){
        return new PaymentCancelDto(
                paymentCancel.getId(),
                paymentCancel.getPaymentUid(),
                paymentCancel.getStatus(),
                paymentCancel.getReason()
        );
    }
}
