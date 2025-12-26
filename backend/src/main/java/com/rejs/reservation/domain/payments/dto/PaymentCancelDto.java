package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import lombok.Getter;

@Getter
public class PaymentCancelDto {
    private Long id;
    private Long reservationId;
    private String paymentUid;
    private PaymentCancelStatus status;
    private PaymentCancelReason reason;

    public boolean isComplete(){
        return !status.equals(PaymentCancelStatus.READY);
    }

    public PaymentCancelDto(Long id, Long reservationId, String paymentUid, PaymentCancelStatus status, PaymentCancelReason reason) {
        this.id = id;
        this.reservationId = reservationId;
        this.paymentUid = paymentUid;
        this.status = status;
        this.reason = reason;
    }

    public static PaymentCancelDto from(PaymentCancel paymentCancel){
        return new PaymentCancelDto(
                paymentCancel.getId(),
                paymentCancel.getReservationId(),
                paymentCancel.getPaymentUid(),
                paymentCancel.getStatus(),
                paymentCancel.getReason()
        );
    }
}
