package com.rejs.reservation.domain.payments.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.payments.dto.PaymentCancelInfo;
import com.rejs.reservation.domain.payments.dto.PaymentInfo;
import com.rejs.reservation.domain.payments.entity.cancel.QPaymentCancel;
import com.rejs.reservation.domain.payments.entity.payment.QPayment;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PaymentInfoQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QPayment payment = QPayment.payment;
    private final QReservation reservation = QReservation.reservation;
    private final QPaymentCancel paymentCancel = QPaymentCancel.paymentCancel;

    public Page<PaymentInfo> getMyPayment(Long username, Pageable pageable){
        List<PaymentInfo> content = jpaQueryFactory
                .select(
                        Projections.constructor(PaymentInfo.class,
                                payment.paymentUid,
                                payment.status,
                                reservation.id,
                                reservation.totalAmount,
                                payment.createdAt,
                                paymentCancel.status,
                                paymentCancel.reason.stringValue(),
                                Projections.constructor(PaymentCancelInfo.class,
                                        paymentCancel.paymentUid,
                                        paymentCancel.status,
                                        paymentCancel.reason.stringValue()
                                )
                        )
                )
                .from(payment)
                .join(payment.reservation, reservation)
                .leftJoin(paymentCancel).on(paymentCancel.payment.id.eq(payment.id))
                .where(reservation.userId.eq(username))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(payment.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(payment.count())
                .from(payment)
                .join(payment.reservation, reservation)
                .where(reservation.userId.eq(username));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }
}
