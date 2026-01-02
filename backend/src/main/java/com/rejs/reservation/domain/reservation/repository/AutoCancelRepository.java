package com.rejs.reservation.domain.reservation.repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.payments.entity.payment.QPayment;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import com.rejs.reservation.domain.reservation.entity.QReservationSeat;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.screening.entity.QScreeningSeat;
import com.rejs.reservation.domain.screening.entity.ScreeningSeatStatus;
import com.rejs.reservation.domain.theater.entity.QSeat;
import com.rejs.reservation.domain.theater.entity.QTheater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Repository
@RequiredArgsConstructor
public class AutoCancelRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final ReservationRepository reservationRepository;

    private QReservation reservation = QReservation.reservation;
    private QReservationSeat reservationSeat = QReservationSeat.reservationSeat;
    private QScreening screening = QScreening.screening;
    private QMovie movie = QMovie.movie;
    private QTheater theater = QTheater.theater;
    private QSeat seat = QSeat.seat;
    private QPayment payment = QPayment.payment;
    private QScreeningSeat screeningSeat = QScreeningSeat.screeningSeat;


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

    public long autoCancel(int limit){
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(20);

        List<Long> targets = reservationRepository.autoCancelReservation(now, threshold, limit);

        if(targets.isEmpty()){
            return 0;
        }

        jpaQueryFactory
                .update(reservation)
                .set(reservation.status, ReservationStatus.CANCELED)
                .where(reservation.id.in(targets)).execute();

        List<Long> screeningSeatIds = jpaQueryFactory
                .select(reservationSeat.screeningSeat.id)
                .from(reservationSeat)
                .where(reservationSeat.reservation.id.in(targets))
                .fetch();

        jpaQueryFactory
                .update(screeningSeat)
                .set(screeningSeat.status, ScreeningSeatStatus.AVAILABLE)
                .where(screeningSeat.id.in(
                        screeningSeatIds
                ))
                .execute();
        return targets.size();
    }
}
