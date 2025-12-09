package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentLogRepository extends JpaRepository<PaymentLog, Long> {
    Optional<PaymentLog> findByPaymentId(String paymentId);
    Optional<PaymentLog> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}
