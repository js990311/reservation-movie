package com.rejs.reservation.domain.screening.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ScreeningExceptionCode implements BusinessExceptionCode {
    SCREENING_TIME_CONFLICT("SCREENING_TIME_CONFILICT", "같은 시간에 상영하는 영화가 이미 있습니다", HttpStatus.CONFLICT),

    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
