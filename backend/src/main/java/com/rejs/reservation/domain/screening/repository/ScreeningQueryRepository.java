package com.rejs.reservation.domain.screening.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rejs.reservation.domain.movie.entity.QMovie;
import com.rejs.reservation.domain.screening.dto.ScreeningWithMovieDto;
import com.rejs.reservation.domain.screening.entity.QScreening;
import com.rejs.reservation.domain.screening.entity.Screening;
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

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Repository
public class ScreeningQueryRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private QScreening screening = QScreening.screening;
    private QMovie movie = QMovie.movie;
    private QTheater theater = QTheater.theater;

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

    public BooleanExpression dateQuery(LocalDate date){
        if(date == null){
            date = LocalDate.now();
        }
        LocalDateTime left = date.atStartOfDay();
        LocalDateTime right = date.atTime(LocalTime.MAX);
        return screening.startTime.between(left, right);
    }
}
