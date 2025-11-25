package com.rejs.reservation.domain.movie.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MovieBusinessExceptionCode implements BusinessExceptionCode {
    MOVIE_NOT_FOUND("MOVIE_NOT_FOUND", "해당하는 영화가 없습니다.", HttpStatus.NOT_FOUND);


    private final String type;
    private final String title;
    private final HttpStatus status;
}
