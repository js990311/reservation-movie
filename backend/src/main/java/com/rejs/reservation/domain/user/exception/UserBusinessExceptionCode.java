package com.rejs.reservation.domain.user.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserBusinessExceptionCode implements BusinessExceptionCode {
    USER_NOT_FOUND("USER_NOT_FOUND", "유저를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("USERNAME_ALREADY_EXISTS", "이미 존재하는 유저이름입니다.", HttpStatus.BAD_REQUEST);

    private final String type;
    private final String title;
    private final HttpStatus status;
}
