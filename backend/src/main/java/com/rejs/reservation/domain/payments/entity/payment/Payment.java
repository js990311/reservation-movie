package com.rejs.reservation.domain.payments.entity.payment;

import com.github.f4b6a3.tsid.TsidCreator;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor
@Getter
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET deleted_at = NOW() WHERE payment_id = ?")
@SQLRestriction("deleted_at IS NULL")
@Entity
public class Payment extends BaseEntity {

    /**
     * 데이터베이스에서만 사용하는 surrogate key
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;


    @Column(name = "payment_uid", columnDefinition = "CHAR(13)")
    private String paymentUid;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus status;

    /* 관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    public Long optionalReservationId(){
        return this.reservation == null ? null : this.reservation.getId();
    }

    public void mapReservation(Reservation reservation){
        this.reservation = reservation;
    }

    // 로직
    public void paid(){
        this.status = PaymentStatus.PAID;
    }

    public void aborted(){
        this.status = PaymentStatus.ABORTED;
    }

    public boolean isCompleted() {
        return status.equals(PaymentStatus.PAID) || status.equals(PaymentStatus.FAILED) || status.equals(PaymentStatus.ABORTED);
    }

    // 생성

    public Payment(String paymentUid, PaymentStatus status) {
        this.paymentUid = paymentUid;
        this.status = status;
    }

    public static Payment create(Reservation reservation){
        Payment payment = new Payment(TsidCreator.getTsid().toString(), PaymentStatus.READY);
        reservation.addPayments(payment);
        return payment;
    }

    public static Payment notFoundPayment(String paymentUid){
        return new Payment(paymentUid, PaymentStatus.ABORTED);
    }

}
