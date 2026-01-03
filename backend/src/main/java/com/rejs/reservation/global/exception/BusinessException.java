package com.rejs.reservation.global.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private Throwable cause;
    private final BusinessExceptionCode code;
    private String detail;

    public BusinessException(Throwable cause, BusinessExceptionCode code, String detail) {
        super(detail, cause);
        this.cause = cause;
        this.code = code;
        this.detail = detail;
    }

    public BusinessException(BusinessExceptionCode code, String detail) {
        super(detail);
        this.code = code;
        this.detail = detail;
    }

    public BusinessException(BusinessExceptionCode code) {
        super(code.getTitle());
        this.code = code;
    }

    public BusinessException(Throwable cause, BusinessExceptionCode code) {
        super(code.getTitle(), cause);
        this.cause = cause;
        this.code = code;
    }


    public static BusinessException of(BusinessExceptionCode code){
        return new BusinessException(code);
    }

    public static BusinessException of(BusinessExceptionCode code, String detail){
        return new BusinessException(code, detail);
    }

    public static BusinessException of(BusinessExceptionCode code, String detail, Throwable cause){
        return new BusinessException(cause, code, detail);
    }

    public static BusinessException of(BusinessExceptionCode code, Throwable cause){
        return new BusinessException(cause, code);
    }


}
