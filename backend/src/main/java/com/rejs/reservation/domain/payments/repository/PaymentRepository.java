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

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE payments p 
        SET p.last_attempted_at = :now, p.status = 'VERIFYING', p.updated_at = NOW()
        WHERE p.payment_uid = :paymentUid
        AND (
            p.status = 'READY' 
            OR 
            (p.status = 'VERIFYING'  AND (
                p.last_attempted_at IS NULL 
                OR 
                p.last_attempted_at <= :threshold
            ))
        )
    """, nativeQuery = true)
    int updateLastAttemptedAt(@Param("paymentUid") String paymentUid, @Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);


    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE payments p 
        SET p.last_attempted_at = :now, p.updated_at = NOW()
        WHERE p.payment_uid = :paymentUid
        AND (
            p.status = 'READY' 
            OR 
            (p.status = 'VERIFYING'  AND (
                p.last_attempted_at IS NULL 
                OR 
                p.last_attempted_at <= :threshold
            ))
        )
    """, nativeQuery = true)
    int updateForCleanUp(@Param("paymentUid") String paymentUid, @Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);

    boolean existsByPaymentUid(@Param("paymentUid") String paymentUid);
}
