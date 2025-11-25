package com.rejs.reservation.global.exception.code;

import org.springframework.http.HttpStatus;

public interface BusinessExceptionCode {
    public String getType();
    public String getTitle();
    public HttpStatus getStatus();
}
