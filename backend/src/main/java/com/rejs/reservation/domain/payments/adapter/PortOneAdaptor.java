package com.rejs.reservation.domain.payments.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelAlreadySuccessException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelFailedException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PaymentCancelRetryableException;
import com.rejs.reservation.domain.payments.adapter.exception.cancel.PortOnePaymentCancelExceptionCode;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.errors.*;
import io.portone.sdk.server.payment.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public PaymentStatusDto getPayment(String paymentId){
        try {
            Payment payment = portOneClient.getPayment(paymentId);
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
        }catch (BusinessException e){
            throw e;
        } catch(Exception e) {
            throw new BusinessException(PaymentExceptionCode.PAYMENT_API_ERROR, "결제 서버로부터 결제정보를 가져오지 못했습니다.");
        }
    }

    @Deprecated
    public void cancelPayment(String paymentId, String reason){
        portOneClient.cancelPayment(paymentId, reason);
    }

    public void cancelPayment(String paymentId, PaymentCancelReason reason){
        try {
            CancelPaymentResponse cancelPaymentResponse = portOneClient.cancelPayment(paymentId, reason.toString());
            if (cancelPaymentResponse.getCancellation() instanceof SucceededPaymentCancellation) {
                log.info("[portone.cancel.success] 결제 취소 성공 paymentId={}", paymentId);
                return;
            }
        }catch (Exception e) {
            throw switch (e) {
                // 이미 취소가 완료됨
                case PaymentAlreadyCancelledException pe ->
                        new PaymentCancelAlreadySuccessException(
                                PortOnePaymentCancelExceptionCode.ALREADY_CANCELLED
                        );

                // 비즈니스 로직상 환불이 불가능한 경우
                case PaymentNotFoundException pe ->
                        new PaymentCancelFailedException(
                                PortOnePaymentCancelExceptionCode.NOT_FOUND, e.getMessage()
                        );
                case PaymentNotPaidException pe ->
                        new PaymentCancelFailedException(
                                PortOnePaymentCancelExceptionCode.NOT_PAID, e.getMessage()
                        );
                case CancelAmountExceedsCancellableAmountException pe ->
                        new PaymentCancelFailedException(
                                PortOnePaymentCancelExceptionCode.INVALID_AMOUNT, e.getMessage()
                        );

                // 일시적 예외로 의심되는 경우
                case PgProviderException pe -> new PaymentCancelRetryableException(
                        PortOnePaymentCancelExceptionCode.PG_PROVIDER_ERROR, pe.getMessage()
                );

                default -> {
                    log.error("[portone.cancel.unknown] 예상치 못한 에러 발생, paymentId={}", paymentId, e);
                    yield new PaymentCancelRetryableException(
                            PortOnePaymentCancelExceptionCode.UNKNOWN_ERROR, e.getMessage()
                    );
                }
            };
        }
    }


    public CustomDataDto extractCustomData(PaidPayment paidPayment){
        String customDataJson = paidPayment.getCustomData();
        if(customDataJson == null){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        CustomDataDto customData;
        try {
            customData = objectMapper.readValue(customDataJson, CustomDataDto.class);
        }catch (JsonProcessingException e){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }catch (Exception e){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        return customData;
    }

}
