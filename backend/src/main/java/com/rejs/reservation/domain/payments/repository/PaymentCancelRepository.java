package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
    Optional<PaymentCancel> findByPaymentUid(String paymentUid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = """
        UPDATE payment_cancels pc 
        SET pc.last_attempted_at = :now, pc.updated_at = NOW() 
        WHERE pc.payment_uid = :paymentUid 
        AND pc.status = 'REQUIRED' 
        AND (
            pc.last_attempted_at IS NULL 
            OR 
            pc.last_attempted_at <= :threshold
        )
    """, nativeQuery = true)
    int updateLastAttemptedAt(@Param("paymentUid") String paymentUid, @Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
}
