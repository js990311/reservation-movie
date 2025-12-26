package com.rejs.reservation.domain.payments.entity.cancel;


import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 엄격하게 관리한다면 payment와의 mapping이 있어야할지도 모르겠다
 */
@NoArgsConstructor
@Getter
@Entity
@Table(name = "payment_cancels")
public class PaymentCancel extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "payment_cancel_id")
    private Long id;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "payment_uid")
    private String paymentUid;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentCancelStatus status;

    @Column
    private PaymentCancelReason reason;

    // 상태변화

    public void failed(){
        this.status = PaymentCancelStatus.FAILED;
    }

    public void canceled(){
        this.status = PaymentCancelStatus.CANCELED;
    }


    // 생성

    public PaymentCancel(Long reservationId, String paymentUid) {
        this.reservationId = reservationId;
        this.paymentUid = paymentUid;
        this.status = PaymentCancelStatus.READY;
    }

    public PaymentCancel(Long reservationId, String paymentUid, PaymentCancelReason reason) {
        this.reservationId = reservationId;
        this.paymentUid = paymentUid;
        this.reason =reason;
        this.status = PaymentCancelStatus.READY;
    }

}
