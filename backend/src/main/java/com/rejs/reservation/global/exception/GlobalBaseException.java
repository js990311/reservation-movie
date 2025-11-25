package com.rejs.reservation.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Deprecated
@Getter
public class GlobalBaseException extends RuntimeException{
    private HttpStatus status;

    public GlobalBaseException(HttpStatus status) {
        this.status = status;
    }
}
