package com.rejs.reservation.domain.theater.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TheaterExceptionCode implements BusinessExceptionCode {
    THEATER_NOT_FOUND("THEATER_NOT_FOUND", "해당하는 영화관이 없습니다", HttpStatus.NOT_FOUND)
    ;
    private final String type;
    private final String title;
    private final HttpStatus status;

}
