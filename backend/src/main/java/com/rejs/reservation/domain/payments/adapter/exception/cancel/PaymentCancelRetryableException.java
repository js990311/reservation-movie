package com.rejs.reservation.domain.payments.adapter.exception.cancel;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

public class PaymentCancelRetryableException extends BusinessException {
    public PaymentCancelRetryableException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public PaymentCancelRetryableException(BusinessExceptionCode code) {
        super(code);
    }
}
