package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p left join fetch p.reservation where p.paymentUid= :paymentUid")
    Optional<Payment> findByPaymentUid(@Param("paymentUid") String paymentUid);

    Optional<Payment> findByReservationIdAndStatus(Long reservationId, PaymentStatus status);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "UPDATE payments " +
            "SET status = 'VERIFYING', updated_at = NOW(6) " +
            "WHERE payment_uid = :paymentId " +
            "AND (" +
            "  status = 'READY' " +
            "  OR (status = 'VERIFYING' AND updated_at < NOW(6) - INTERVAL 5 MINUTE)" +
            ")",
            nativeQuery = true)
    int tryToUpdate(String paymentId);
}
