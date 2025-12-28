package com.rejs.reservation.domain.payments.adapter.exception.cancel;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

public class PaymentCancelAlreadySuccessException extends BusinessException {
    public PaymentCancelAlreadySuccessException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public PaymentCancelAlreadySuccessException(BusinessExceptionCode code) {
        super(code);
    }
}
