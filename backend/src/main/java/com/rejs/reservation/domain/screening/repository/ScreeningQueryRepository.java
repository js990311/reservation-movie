package com.rejs.reservation.domain.screening.repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.reservation.entity.QReservation;
import com.rejs.reservation.domain.reservation.entity.QReservationSeat;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.screening.dto.ScreeningDetailDto;
import com.rejs.reservation.domain.screening.dto.ScreeningSeatDto;
import com.rejs.reservation.domain.screening.dto.ScreeningWithMovieDto;
import com.rejs.reservation.domain.screening.dto.ScreeningWithTheaterDto;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.theater.entity.QSeat;
import com.rejs.reservation.domain.theater.entity.QTheater;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ScreeningQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QScreening screening = QScreening.screening;
    private QMovie movie = QMovie.movie;
    private QTheater theater = QTheater.theater;
    private QSeat seat = QSeat.seat;
    private QReservationSeat reservationSeat = QReservationSeat.reservationSeat;
    private QReservation reservation = QReservation.reservation;

    public List<ScreeningSeatDto> findScreeningSeats(Long screeningId, Long theaterId){
            return jpaQueryFactory
                    .select(
                            Projections.constructor(
                                    ScreeningSeatDto.class,
                                    seat.id,
                                    seat.rowNum,
                                    seat.colNum,
                                    new CaseBuilder().when(
                                            JPAExpressions
                                                    .selectOne()
                                                    .from(reservationSeat) // 예약된 좌석 중에
                                                    .leftJoin(reservationSeat.reservation, reservation)
                                                    .where(
                                                            reservationSeat.seatId.eq(seat.id) // seatId에 대한 예약이면서
                                                            .and(reservation.screeningId.eq(screeningId)) // 이번 상영표에 대한 예약이면서
                                                            .and(reservation.status.ne(ReservationStatus.CANCELED)) // 취소상태가 아닌 것이
                                                    ).exists() // 존재하는가?
                                    ).then(true).otherwise(false) // 존재하면 isReserved=true (이미 예약됨)
                            )
                    )
                    .from(seat)
                    .where(seat.theater.id.eq(theaterId))
                    .fetch()
            ;
    }

    public Optional<Screening> findById(Long id) {
        Screening entity = jpaQueryFactory
                .select(
                        screening
                ).from(screening)
                .join(screening.theater, theater).fetchJoin()
                .join(screening.movie, movie).fetchJoin()
                .where(screening.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(entity);
    }

    public List<ScreeningWithMovieDto> findByTheaterId(Long theaterId, LocalDate date){
        return jpaQueryFactory
                .select(Projections.constructor(
                        ScreeningWithMovieDto.class,
                        screening.id,
                        screening.theaterId,
                        screening.startTime,
                        screening.endTime,
                        movie.id,
                        movie.title,
                        movie.duration
                )).from(
                        screening
                ).join(
                        screening.movie, movie
                ).where(
                        screening.theaterId.eq(theaterId)
                                .and(dateQuery(date))
                )
                .orderBy(
                        screening.startTime.asc()
                )
                .fetch();
    }

    public List<ScreeningWithTheaterDto> findByMovieId(Long movieId, LocalDate date){
        return jpaQueryFactory
                .select(Projections.constructor(
                        ScreeningWithTheaterDto.class,
                        screening.id,
                        screening.movieId,
                        screening.startTime,
                        screening.endTime,
                        theater.id,
                        theater.name
                ))
                .from(screening)
                .join(screening.theater, theater)
                .where(screening.movieId.eq(movieId).and(dateQuery(date)))
                .fetch();
    }


    public BooleanExpression dateQuery(LocalDate date){
        if(date == null){
            date = LocalDate.now();
        }
        LocalDateTime left = date.atStartOfDay();
        LocalDateTime right = date.atTime(LocalTime.MAX);
        return screening.startTime.between(left, right);
    }
}
