package com.rejs.reservation.domain.payments.exception;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

public class GetPaymentInfoFailException extends BusinessException {
    public GetPaymentInfoFailException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public GetPaymentInfoFailException(Throwable cause, BusinessExceptionCode code, String detail) {
        super(cause, code, detail);
    }

    public GetPaymentInfoFailException(BusinessExceptionCode code) {
        super(code);
    }

    public GetPaymentInfoFailException(Throwable cause, BusinessExceptionCode code) {
        super(cause, code);
    }
}
