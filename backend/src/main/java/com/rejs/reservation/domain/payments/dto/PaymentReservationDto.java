package com.rejs.reservation.domain.payments.dto;

import com.rejs.reservation.domain.reservation.dto.ReservationDetailDto;
import lombok.Getter;

@Getter
public class PaymentReservationDto {
    /**
     * 주문명. 자유입력
     */
    private String orderName;

    /**
     * 비용
     */
    private Integer totalAmount;
    /**
     * ISO 4217 기준 표준 알파벳 통화코드
     */
    private String currency;
}
