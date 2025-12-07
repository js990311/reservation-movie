package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.PaymentLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentLog, Long> {
    Optional<PaymentLog> findByPaymentId(String paymentId);
}
