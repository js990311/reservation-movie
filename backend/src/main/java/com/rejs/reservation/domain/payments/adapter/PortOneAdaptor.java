package com.rejs.reservation.domain.payments.adapter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.payment.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 외부 호출에 대한 추상화
 */
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

    public void cancelPayment(String paymentId, String reason){
        portOneClient.cancelPayment(paymentId, reason);
    }

    public void cancelPayment(String paymentId, PaymentCancelReason reason){
        CancelPaymentResponse cancelPaymentResponse = portOneClient.cancelPayment(paymentId, reason.toString());
        if(cancelPaymentResponse.getCancellation() instanceof SucceededPaymentCancellation){
            return;
        }else {
            throw new BusinessException(PaymentExceptionCode.PAYMENT_CANCEL_FAIL);
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
