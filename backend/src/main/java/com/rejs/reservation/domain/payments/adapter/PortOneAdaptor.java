package com.rejs.reservation.domain.payments.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.exception.GetPaymentInfoFailException;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.exception.cancel.PortOnePaymentCancelExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.portone.sdk.server.errors.*;
import io.portone.sdk.server.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * 외부 호출에 대한 추상화
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PortOneAdaptor {
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;
    private final PortOneClient portOneClient;

    @WithSpan("portone.payment.get")
    public CompletableFuture<PaymentStatusDto> getPayment(String paymentId){
        return portOneClient.getPayment(paymentId)
                .thenApply(payment-> {
                    if(payment instanceof PaidPayment paidPayment){
                        return PaymentStatusDto.builder()
                                .customData(extractCustomData(paidPayment))
                                .totalAmount(paidPayment.getAmount().getTotal())
                                .channel(paidPayment.getChannel().getType())
                                .currency(paidPayment.getCurrency())
                                .build();
                    }else {
                        throw BusinessException.of(PaymentExceptionCode.INVALID_PAYMENT_STATE);
                    }
                })
                .exceptionally(e->{
                    log.warn("[portOneAdaptor.getPayment.exception] 결제 정보 가져오는 데 실패했습니다. paymentId={}",paymentId, e);
                    Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
                    throw switch (cause) {
                        // 1. [RETRY] 일시적 장애 (Unknown) -> 재시도
                        case UnknownException ue -> new GetPaymentInfoFailException(ue, PaymentExceptionCode.PAYMENT_API_ERROR);

                        // 2. [FAIL] 데이터 없음 (Not Found) -> 404
                        case PaymentNotFoundException pe -> new GetPaymentInfoFailException(pe, PaymentExceptionCode.PAYMENT_NOT_FOUND);

                        // 3. [FAIL] 잘못된 요청 (Invalid Request) -> 400
                        case InvalidRequestException ire -> new GetPaymentInfoFailException(ire, PaymentExceptionCode.INVALID_PAYMENT_STATE);

                        // 4. [FAIL] 인증/권한 오류 (Auth/Forbidden) -> 500 (서버 설정 문제)
                        case UnauthorizedException ue -> new GetPaymentInfoFailException(ue, PaymentExceptionCode.PAYMENT_API_ERROR);
                        case ForbiddenException fe -> new GetPaymentInfoFailException(fe, PaymentExceptionCode.PAYMENT_API_ERROR);

                        // [DEFAULT] 시스템 에러
                        default -> new GetPaymentInfoFailException(PaymentExceptionCode.UNKNOWN_EXCEPTION);
                    };
                });
    }

    @WithSpan("portone.payment.cancel")
    public CompletableFuture<Boolean> cancelPayment(String paymentId, PaymentCancelReason reason){
        return portOneClient.cancelPayment(paymentId, reason.toString()).thenApply(cancelPaymentResponse -> {
            if (cancelPaymentResponse.getCancellation() instanceof SucceededPaymentCancellation) {
                log.info("[portone.cancel.success] 결제 취소 성공 paymentId={}", paymentId);
                return true;
            }else if (cancelPaymentResponse.getCancellation() instanceof FailedPaymentCancellation failedPaymentCancellation){
                log.info("[ACTION_REQUIRED] [portone.cancel.fail] 결제 취소 실패 paymentId={} paymentCancelId={} reason={}", paymentId, failedPaymentCancellation.getId(), failedPaymentCancellation.getReason());
                throw new PaymentCancelFailedException(PortOnePaymentCancelExceptionCode.UNKNOWN_ERROR, failedPaymentCancellation.getReason());
            }else if(cancelPaymentResponse.getCancellation() instanceof RequestedPaymentCancellation){
                log.info("[portone.cancel.requested] 결제 취소 요청됨 paymentId={}", paymentId);
                throw new PaymentCancelRetryableException(PortOnePaymentCancelExceptionCode.UNKNOWN_ERROR);
            }
            return false;
        }).exceptionally(e->{
            Throwable cause = (e instanceof CompletionException) ? e.getCause() : e;
            throw switch (cause) {
                // 결제한적 없거나 이미 성공함
                case PaymentAlreadyCancelledException pe -> handleCancelAlreadySuccess(pe, paymentId);
                case PaymentNotPaidException pe -> handleCancelAlreadySuccess(pe, paymentId);

                // 재시도 해야하는 예외들
                case PgProviderException pe -> handleCancelRetryable(pe, paymentId, "PG사 응답 오류");
                case UnknownException ue -> handleCancelRetryable(ue, paymentId, "PortOne 알 수 없는 오류");

                // 위에 예외에 해당하지 않으면 실패임
                case CancelPaymentException cpe -> handleCancelFailed(cpe, paymentId, "비즈니스 로직 오류");

                // sdk가 만든 예외가 아님
                default -> handleCancelUnknown(cause, paymentId);
            };
        });
    }

    public CustomDataDto extractCustomData(PaidPayment paidPayment){
        String customDataJson = paidPayment.getCustomData();
        if(customDataJson == null){
            throw new GetPaymentInfoFailException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        CustomDataDto customData;
        try {
            customData = objectMapper.readValue(customDataJson, CustomDataDto.class);
        }catch (JsonProcessingException e){
            throw new GetPaymentInfoFailException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }catch (Exception e){
            throw new GetPaymentInfoFailException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        return customData;
    }

    public BusinessException handleCancelAlreadySuccess(Throwable cause, String paymentId){
        log.info("[portone.cancel] 이미 처리된 요청입니다. paymentId={}, detail={}",paymentId, cause.getClass().getSimpleName(), cause);
        return new PaymentCancelAlreadySuccessException(PortOnePaymentCancelExceptionCode.ALREADY_CANCELLED);
    }

    private BusinessException handleCancelRetryable(Throwable cause, String paymentId, String description) {
        log.warn("[portone.cancel.retry] {}. paymentId={}, cause={}",
                description, paymentId, cause.getMessage());
        return new PaymentCancelRetryableException(
                PortOnePaymentCancelExceptionCode.PG_PROVIDER_ERROR, cause.getMessage()
        );
    }

    private BusinessException handleCancelFailed(CancelPaymentException cause, String paymentId, String description) {
        log.error("[ACTION_REQUIRED] [portone.cancel.fail] {}. 수동 확인이 필요합니다. paymentId={}, type={}, cause={}",
                description, paymentId, cause.getClass().getSimpleName(), cause.getMessage());
        return new PaymentCancelFailedException(
                PortOnePaymentCancelExceptionCode.LOGIC_ERROR, cause.getMessage()
        );
    }

    private BusinessException handleCancelUnknown(Throwable cause, String paymentId) {
        log.error("[portone.cancel.unknown] 예상치 못한 시스템 에러. paymentId={}, cause={}", paymentId, cause);
        return new PaymentCancelRetryableException(
                PortOnePaymentCancelExceptionCode.UNKNOWN_ERROR, cause.getMessage()
        );
    }
}
