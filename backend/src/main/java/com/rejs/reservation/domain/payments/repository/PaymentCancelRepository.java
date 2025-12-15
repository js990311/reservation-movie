package com.rejs.reservation.domain.payments.repository;

import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
}
