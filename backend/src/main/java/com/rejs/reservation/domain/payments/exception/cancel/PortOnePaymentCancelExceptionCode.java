package com.rejs.reservation.domain.payments.exception.cancel;

import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum PortOnePaymentCancelExceptionCode implements BusinessExceptionCode {
    // 1. 즉시 완료 처리 (Already Done)
    ALREADY_CANCELLED("PO_ALREADY_CANCELLED", "이미 취소된 결제입니다.", HttpStatus.OK),

    // 2. 환불 불가능 (FAILED 상태로 전이 - 좀비 데이터 방지)
    NOT_FOUND("PO_NOT_FOUND", "결제 건을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NOT_PAID("PO_NOT_PAID", "결제되지 않은 주문은 취소할 수 없습니다.", HttpStatus.BAD_REQUEST),
    INVALID_AMOUNT("PO_INVALID_AMOUNT", "취소 금액이 올바르지 않거나 잔액이 부족합니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN("PO_FORBIDDEN", "취소 권한이 없거나 상점 설정이 올바르지 않습니다.", HttpStatus.FORBIDDEN),
    LOGIC_ERROR("PO_LOGIC_ERROR", "취소 관련 로직이 실패하여 취소할 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR),

    // 3. 재시도 필요 (REQUIRED 상태 유지 - 시스템/네트워크 문제)
    PG_PROVIDER_ERROR("PO_PROVIDER_ERROR", "PG사 통신 중 오류가 발생했습니다.", HttpStatus.SERVICE_UNAVAILABLE),
    NETWORK_ERROR("PO_NETWORK_ERROR", "포트원 API 통신 중 네트워크 오류가 발생했습니다.", HttpStatus.GATEWAY_TIMEOUT),
    UNKNOWN_ERROR("PO_UNKNOWN_ERROR", "알 수 없는 에러가 발생했습니다. 재시도가 필요합니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String type;
    private final String title;
    private final HttpStatus status;

}
