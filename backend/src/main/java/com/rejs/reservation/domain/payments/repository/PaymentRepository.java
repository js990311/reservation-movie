package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
