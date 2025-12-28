package com.rejs.reservation.domain.payments.adapter.exception.cancel;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

/**
 * 결제 취소가 불가능한 예외
 */
public class PaymentCancelFailedException extends BusinessException {
    public PaymentCancelFailedException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public PaymentCancelFailedException(BusinessExceptionCode code) {
        super(code);
    }
}
