package com.rejs.reservation.global.security.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthenticationExceptionCode implements BusinessExceptionCode {
    // 인증 관련
    INVALID_TOKEN("INVALID_TOKEN", "잘못된 토큰입니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("ACCESS_DENIED", "권한이 없습니다", HttpStatus.FORBIDDEN),
    USER_INFO_MISMATCH("USER_INFO_MISMATCH", "유저정보가 맞지 않습니다", HttpStatus.UNAUTHORIZED),
    REFRESH_TOKEN_REQUIRED("REFRESH_TOKEN_REQUIRED", "REFRESH TOKEN이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_PATH("INVALID_PATH", "존재하지 않는 페이지입니다.", HttpStatus.NOT_FOUND),
    INVALID_REFRESH_TOKEN("INVALID_REFRESH_TOKEN","잘못된 refresh 토큰입니다", HttpStatus.UNAUTHORIZED)
    ;

    private String type;
    private String title;
    private HttpStatus status;
}
