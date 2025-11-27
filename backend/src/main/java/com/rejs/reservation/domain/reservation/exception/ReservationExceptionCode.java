package com.rejs.reservation.domain.reservation.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationExceptionCode implements BusinessExceptionCode {
    INVALID_OR_UNAVAILABLE_SEATS("INVALID_OR_UNAVAILABLE_SEATS", "이용가능한 좌석이 아닙니다.", HttpStatus.BAD_REQUEST);

    private final String type;
    private final String title;
    private final HttpStatus status;
}
