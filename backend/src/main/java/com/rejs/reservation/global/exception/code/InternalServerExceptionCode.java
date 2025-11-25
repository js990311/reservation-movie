package com.rejs.reservation.global.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InternalServerExceptionCode implements BusinessExceptionCode{
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_EXCEPTION", "서버 내부에서 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
