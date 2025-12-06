package com.rejs.reservation.domain.reservation.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.reservation.dto.ReservationDetailDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSeatNumberDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import com.rejs.reservation.domain.reservation.entity.QReservationSeat;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.theater.entity.QSeat;
import com.rejs.reservation.domain.theater.entity.QTheater;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public Page<ReservationSummaryDto> findMyReservations(Long userId, Pageable pageable){
        List<ReservationSummaryDto> contents = jpaQueryFactory
                .select(
                        Projections.constructor(
                                ReservationSummaryDto.class,
                                reservation.id,
                                reservation.status,
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
                .join(seat).on(reservationSeat.seatId.eq(seat.id))
                .where(reservationSeat.reservation.id.eq(id)).fetch();
    }
}
