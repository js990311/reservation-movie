package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentUid(String paymentUid);

    Optional<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}
