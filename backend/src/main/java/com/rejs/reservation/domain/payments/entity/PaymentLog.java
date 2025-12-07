package com.rejs.reservation.domain.payments.entity;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "payment_logs")
public class PaymentLog {
    @Id
    @GeneratedValue
    @Column(name = "payment_log_id")
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Enumerated(EnumType.STRING)
    @Column
    private PaymentStatus status;

    @Column
    private String reason;

    public void mapReservaiton(Reservation reservation){
        this.reservationId = reservation.getId();
    }

    public void success(){
        this.status = PaymentStatus.PAID;
    }

    public void failed(){
        this.status = PaymentStatus.FAILED;
    }

    public PaymentLog(String paymentId) {
        this.paymentId = paymentId;
        this.status = PaymentStatus.READY;
    }
}
