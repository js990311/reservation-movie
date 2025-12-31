package com.rejs.reservation.domain.payments.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelStatus;
import com.rejs.reservation.domain.payments.entity.cancel.QPaymentCancel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Repository
public class PaymentCancelQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPaymentCancel paymentCancel = QPaymentCancel.paymentCancel;

    public List<String> findAbandonedPaymentCancels(LocalDateTime threshold, int limit){
        return jpaQueryFactory
                .select(paymentCancel.paymentUid)
                .from(paymentCancel)
                .where(
                        paymentCancel.status.eq(PaymentCancelStatus.REQUIRED)
                                .and(
                                        paymentCancel.lastAttemptedAt.isNull().or(
                                                paymentCancel.lastAttemptedAt.before(threshold)
                                        )
                                )
                ).limit(limit).fetch();
    }
}
