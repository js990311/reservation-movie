package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    public Optional<Payment> findByPaymentUid(String paymentUid);
}
