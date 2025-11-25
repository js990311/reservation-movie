package com.rejs.reservation.global.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final BusinessExceptionCode code;
    private String detail;

    public BusinessException(BusinessExceptionCode code, String detail) {
        super(detail);
        this.code = code;
        this.detail = detail;
    }

    public BusinessException(BusinessExceptionCode code) {
        super(code.getTitle());
        this.code = code;
    }

    public static BusinessException of(BusinessExceptionCode code){
        return new BusinessException(code);
    }

    public static BusinessException of(BusinessExceptionCode code, String detail){
        return new BusinessException(code, detail);
    }
}
