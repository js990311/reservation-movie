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
    PAYMENT_INFO_MISMATCH("PAYMENT_INFO_MISMATCH", "결제 정보가 실제 예매와 맞지 않습니다", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND("RESERVATION_NOT_FOUND", "결제 정보와 매핑된 예매 정보가 없습니다", HttpStatus.NOT_FOUND),
    INVALID_CURRENCY("INVALID_CURRENCY", "지원되지 앟는 통화입니다", HttpStatus.BAD_REQUEST),
    INVALID_CHANNEL("INVALID_CHANNEL", "유효하지 않은 결제환경입니다", HttpStatus.BAD_REQUEST),
    PAYMENT_VALIDATION_FAIL("PAYMENT_VALIDATION_FAIL", "결제 로직이 정상 수행되지 않았습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    PAYMENT_CANCEL_FAIL("PAYMENT_CANCEL_FAIL", "결제 취소 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_PAID_RESERVATION("ALREADY_PAID_RESERVATION", "이미 결제된 예매입니다", HttpStatus.CONFLICT),
    PAYMENT_NOT_FOUND("PAYMENT_NOT_FOUND", "존재하지 않는 결제 내역입니다", HttpStatus.NOT_FOUND),
    RESERVATION_ALREADY_CANCELED("RESERVATION_ALREADY_CANCELED", "결제가 이미 실패했습니다", HttpStatus.CONFLICT)
    ;

    private final String type;
    private final String title;
    private final HttpStatus status;
}
