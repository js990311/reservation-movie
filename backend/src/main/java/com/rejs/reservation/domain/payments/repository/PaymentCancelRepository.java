package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
    Optional<PaymentCancel> findByPaymentUid(String paymentUid);
}
