package com.rejs.reservation.domain.reservation.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.payments.entity.payment.QPayment;
import com.rejs.reservation.domain.reservation.dto.ReservationSeatNumberDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import com.rejs.reservation.domain.reservation.entity.QReservationSeat;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.screening.entity.QScreeningSeat;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.screening.entity.ScreeningSeatStatus;
import com.rejs.reservation.domain.theater.entity.QSeat;
import com.rejs.reservation.domain.theater.entity.QTheater;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ReservationQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QReservation reservation = QReservation.reservation;
    private QReservationSeat reservationSeat = QReservationSeat.reservationSeat;
    private QScreening screening = QScreening.screening;
    private QMovie movie = QMovie.movie;
    private QTheater theater = QTheater.theater;
    private QSeat seat = QSeat.seat;
    private QPayment payment = QPayment.payment;
    private QScreeningSeat screeningSeat = QScreeningSeat.screeningSeat;

    public Page<ReservationSummaryDto> findMyReservations(Long userId, Pageable pageable){
        List<ReservationSummaryDto> contents = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReservationSummaryDto.class,
                                reservation.id,
                                reservation.status,
                                reservation.totalAmount,
                                screening.id,
                                screening.startTime,
                                screening.endTime,
                                movie.id,
                                movie.title,
                                theater.id,
                                theater.name
                        )
                )
                .from(
                        reservation
                ).join(
                        screening
                ).on(reservation.screeningId.eq(screening.id))
                .join(
                        screening.movie, movie
                ).join(
                        screening.theater, theater
                ).where(
                        reservation.userId.eq(userId)
                ).offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countq = jpaQueryFactory
                .select(reservation.id.count())
                .from(reservation)
                .where(
                        reservation.userId.eq(userId)
                );

        return PageableExecutionUtils.getPage(contents, pageable, countq::fetchOne);
    }

    public ReservationSummaryDto findById(Long id){
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReservationSummaryDto.class,
                                reservation.id,
                                reservation.status,
                                reservation.totalAmount,
                                screening.id,
                                screening.startTime,
                                screening.endTime,
                                movie.id,
                                movie.title,
                                theater.id,
                                theater.name
                        )
                )
                .from(
                        reservation
                ).join(
                       screening
                ).on(reservation.screeningId.eq(screening.id))
                .join(
                        screening.movie, movie
                ).join(
                        screening.theater, theater
                ).where(
                        reservation.id.eq(id)
                )
                .fetchOne()
        ;
    }

    public List<ReservationSeatNumberDto> findSeatNumberById(Long id){
        return jpaQueryFactory
                .select(
                        Projections.constructor(
                            ReservationSeatNumberDto.class,
                            seat.rowNum,
                            seat.colNum
                        )
                )
                .from(reservationSeat)
                .join(reservationSeat.screeningSeat, screeningSeat)
                .join(screeningSeat.seat, seat)
                .where(reservationSeat.reservation.id.eq(id)).fetch();
    }

    public Optional<Reservation> findForCancel(Long id) {
        return Optional.ofNullable(jpaQueryFactory
                .select(
                        reservation
                ).from(reservation)
                .join(screening).on(reservation.screeningId.eq(screening.id)) // 상영시간 종료 쿼리를 위한
                .join(reservation.payments, payment).fetchJoin()
                .where(
                        reservation.id.eq(id)
                                .and(reservation.status.ne(ReservationStatus.CANCELED)) // 이미 취소상태가 아닌
                                .and(screening.startTime.after(LocalDateTime.now()))
                )
                .fetchOne()
        );
    }

    public List<ScreeningSeat> selectAvailableSeats(List<Long> seatIds) {
        return jpaQueryFactory
                .select(screeningSeat)
                .from(screeningSeat)
                .join(screeningSeat.screening, screening)
                .where(
                        screeningSeat.id.in(seatIds),
                        screeningSeat.status.eq(ScreeningSeatStatus.AVAILABLE),
                        screening.startTime.gt(LocalDateTime.now()),
                        screening.deletedAt.isNull()
                )
                .orderBy(screeningSeat.id.asc())
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetch();
    }

    public void updateScreeningSeatAvailable(Long reservationId){
        List<Long> screeningSeatIds = jpaQueryFactory
                .select(reservationSeat.screeningSeat.id)
                .from(reservationSeat)
                .where(reservationSeat.reservation.id.in(reservationId))
                .fetch();

        jpaQueryFactory
                .update(screeningSeat)
                .set(screeningSeat.status, ScreeningSeatStatus.AVAILABLE)
                .where(screeningSeat.id.in(
                        screeningSeatIds
                ))
                .execute();
        return;
    }
}
