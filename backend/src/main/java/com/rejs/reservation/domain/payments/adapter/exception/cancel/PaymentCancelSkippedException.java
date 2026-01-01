package com.rejs.reservation.domain.payments.adapter.exception.cancel;

import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;

/**
 * 결제 취소를 할 필요가 없는 예외
 */
public class PaymentCancelSkippedException extends BusinessException {

    public PaymentCancelSkippedException(BusinessExceptionCode code, String detail) {
        super(code, detail);
    }

    public PaymentCancelSkippedException(BusinessExceptionCode code) {
        super(code);
    }
}
