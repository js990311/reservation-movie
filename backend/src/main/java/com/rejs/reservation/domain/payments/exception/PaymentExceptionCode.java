package com.rejs.reservation.domain.payments.exception;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PaymentExceptionCode implements BusinessExceptionCode {
    INVALID_PAYMENT_STATE("INVALID_PAYMENT_STATE", "적절한 결제 상태가 아닙니다", HttpStatus.BAD_REQUEST),
    MISSING_CUSTOM_DATA("MISSING_CUSTOM_DATA", "필수 메타데이터(Custom Data)에 대한 정보를 확인할 수 없습니다", HttpStatus.BAD_REQUEST),
    PAYMENT_API_ERROR("PAYMENT_API_ERROR", "결제 검증 중 알 수 없는 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_AMOUNT_MISMATCH("PAYMENT_AMOUNT_MISMATCH", "결제 금액이 실제 금액과 맞지 않습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", "결제 정보와 매핑된 예매 정보가 없습니다", HttpStatus.NOT_FOUND),
    INVALID_CURRENCY("INVALID_CURRENCY", "지원되지 앟는 통화입니다", HttpStatus.BAD_REQUEST),
    INVALID_CHANNEL("INVALID_CHANNEL", "유효하지 않은 결제환경입니다", HttpStatus.BAD_REQUEST),
    PAYMENT_VALIDATION_FAIL("PAYMENT_VALIDATION_FAIL", "결제 로직이 정상 수행되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR)
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
