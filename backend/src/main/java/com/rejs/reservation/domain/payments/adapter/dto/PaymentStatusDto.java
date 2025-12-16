package com.rejs.reservation.domain.payments.adapter.dto;

import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PaymentStatusDto {
    private final CustomDataDto customData;
    private final Long totalAmount;
    private final SelectedChannelType channel;
    private final Currency currency;

    public void validate(){
        if(!channel.equals(SelectedChannelType.Test.INSTANCE)){
            throw new BusinessException(PaymentExceptionCode.INVALID_CHANNEL);
        }
        if(!currency.equals(Currency.Krw.INSTANCE)){
            throw new BusinessException(PaymentExceptionCode.INVALID_CURRENCY);
        }
    }
}
