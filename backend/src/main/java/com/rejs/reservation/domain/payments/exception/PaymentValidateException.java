package com.rejs.reservation.domain.payments.exception;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

public class PaymentValidateException extends BusinessException {
    public PaymentValidateException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public PaymentValidateException(BusinessExceptionCode code) {
        super(code);
    }
}
