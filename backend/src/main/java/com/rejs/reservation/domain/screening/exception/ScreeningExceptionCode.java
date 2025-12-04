package com.rejs.reservation.domain.screening.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ScreeningExceptionCode implements BusinessExceptionCode {
    SCREENING_TIME_CONFLICT("SCREENING_TIME_CONFLICT", "같은 시간에 상영하는 영화가 이미 있습니다", HttpStatus.CONFLICT),
    SCREENING_NOT_FOUND("SCREENING_NOT_FOUND", "해당 상영표가 없습니다", HttpStatus.NOT_FOUND)
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
