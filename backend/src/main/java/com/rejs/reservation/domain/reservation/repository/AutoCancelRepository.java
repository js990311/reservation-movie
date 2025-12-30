package com.rejs.reservation.domain.reservation.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.payments.entity.payment.QPayment;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import com.rejs.reservation.domain.reservation.entity.QReservationSeat;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.theater.entity.QSeat;
import com.rejs.reservation.domain.theater.entity.QTheater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Repository
@RequiredArgsConstructor
public class AutoCancelRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QReservation reservation = QReservation.reservation;
    private QReservationSeat reservationSeat = QReservationSeat.reservationSeat;
    private QScreening screening = QScreening.screening;
    private QMovie movie = QMovie.movie;
    private QTheater theater = QTheater.theater;
    private QSeat seat = QSeat.seat;
    private QPayment payment = QPayment.payment;


    public long autoCancelByCreatedAt(){
        return jpaQueryFactory
                .update(reservation)
                .set(reservation.status, ReservationStatus.CANCELED)
                .where(
                        reservation.status.eq(ReservationStatus.PENDING),
                        reservation.createdAt.before(LocalDateTime.now().minusMinutes(10))
                ).execute();
    }

    public long autoCancelByScreeningStartTime(){
        return jpaQueryFactory
                .update(reservation)
                .set(reservation.status, ReservationStatus.CANCELED)
                .where(
                        reservation.status.eq(ReservationStatus.PENDING),
                        reservation.screeningId.in(
                                JPAExpressions
                                        .select(screening.id)
                                        .from(screening)
                                        .where(screening.startTime.before(LocalDateTime.now()))
                        )
                ).execute();
    }

}
