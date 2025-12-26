package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p join fetch p.reservation where p.paymentUid= :paymentUid")
    Optional<Payment> findByPaymentUid(@Param("paymentUid") String paymentUid);

    Optional<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);
}
