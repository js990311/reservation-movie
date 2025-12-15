package com.rejs.reservation.domain.payments.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.entity.QPaymentLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReservationPaymentRepository{
    private final JPAQueryFactory jpaQueryFactory;

    private QPaymentLog paymentLog = QPaymentLog.paymentLog;

    public boolean existsPaymentByReservationId(Long reservationId){
        Integer i = jpaQueryFactory
                .selectOne()
                .from(paymentLog)
                .where(paymentLog.reservationId.eq(reservationId).and(
                        paymentLog.status.eq(PaymentStatus.PAID)
                )).fetchFirst();
        return i != null;
    }
}
