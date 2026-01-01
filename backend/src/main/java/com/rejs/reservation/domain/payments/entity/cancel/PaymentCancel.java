package com.rejs.reservation.domain.payments.entity.cancel;


import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * 엄격하게 관리한다면 payment와의 mapping이 있어야할지도 모르겠다
 */
@NoArgsConstructor
@Getter
@Entity
@SQLDelete(sql = "UPDATE payment_cancels SET deleted_at = NOW() WHERE payment_cancel_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Table(name = "payment_cancels")
public class PaymentCancel extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_cancel_id")
    private Long id;

    @Column(name = "payment_uid", columnDefinition = "CHAR(13)")
    private String paymentUid;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentCancelStatus status;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentCancelReason reason;

    @Column
    private LocalDateTime lastAttemptedAt;

    // 관계
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    public void canceled(){
        this.status = PaymentCancelStatus.CANCELED;
    }

    public void failed() {
        this.status = PaymentCancelStatus.FAILED;
    }

    public void skipped() {
        this.status = PaymentCancelStatus.SKIPPED;
    }

    // 생성

    public PaymentCancel(Payment payment, PaymentCancelReason reason) {
        this.payment = payment;
        this.paymentUid = payment.getPaymentUid();
        this.reason =reason;
        this.status = PaymentCancelStatus.REQUIRED;
    }

}
