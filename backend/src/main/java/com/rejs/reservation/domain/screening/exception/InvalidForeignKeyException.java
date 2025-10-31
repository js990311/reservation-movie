package com.rejs.reservation.domain.screening.exception;

import com.rejs.reservation.global.exception.GlobalBaseException;
import org.springframework.http.HttpStatus;

/**
 * entity 생성시 잘못된 요청을 보낸 경우
 */
public class InvalidForeignKeyException extends GlobalBaseException {
    public InvalidForeignKeyException() {
        super(HttpStatus.BAD_REQUEST);
    }
}
