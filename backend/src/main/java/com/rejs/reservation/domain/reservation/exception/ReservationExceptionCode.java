package com.rejs.reservation.domain.reservation.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ReservationExceptionCode implements BusinessExceptionCode {
    INVALID_OR_UNAVAILABLE_SEATS("INVALID_OR_UNAVAILABLE_SEATS", "이용가능한 좌석이 아닙니다.", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", "존재하지 않는 예매내역입니다", HttpStatus.NOT_FOUND),
    NOT_RESERVATION_OWNER("NOT_RESERVATION_OWNER", "이 예매의 주인이 아닙니다", HttpStatus.FORBIDDEN),
    INVALID_RESERVATION_CANCEL_REQUEST("INVALID_RESERVATION_CANCEL_REQUEST", "취소 가능한 상태가 아니거나 상영 시간이 만료되었습니다.", HttpStatus.BAD_REQUEST)
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
