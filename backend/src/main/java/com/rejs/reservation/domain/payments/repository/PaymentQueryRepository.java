package com.rejs.reservation.domain.payments.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.entity.payment.QPayment;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PaymentQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private QPayment payment = QPayment.payment;
    private QReservation reservation = QReservation.reservation;

    @Transactional(readOnly = true)
    public List<String> findZombiePayment(LocalDateTime threshold, int limit){
        return jpaQueryFactory
                .select(payment.paymentUid)
                .from(payment)
                .where(
                        payment.status.in(PaymentStatus.READY, PaymentStatus.VERIFYING),
                        payment.updatedAt.before(threshold)
        ).limit(limit)
                .fetch();
    }

}
