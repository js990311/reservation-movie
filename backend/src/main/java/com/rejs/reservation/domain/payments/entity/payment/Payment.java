package com.rejs.reservation.domain.payments.entity.payment;

import com.github.f4b6a3.tsid.TsidCreator;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "payments")
@Entity
public class Payment extends BaseEntity {

    /**
     * 데이터베이스에서만 사용하는 surrogate key
     */
    @Id
    @GeneratedValue
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "payment_uid")
    private String paymentUid;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus status;

    // 로직
    public void paid(){
        this.status = PaymentStatus.PAID;
    }

    // 생성

    public Payment(Long reservationId) {
        this.reservationId = reservationId;
        this.status = PaymentStatus.READY;
        this.paymentUid = TsidCreator.getTsid().toString();
    }
}
