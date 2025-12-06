package com.rejs.reservation.domain.payments.dto;

public class PaymentDto<T> {
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

    /**
     * 주문자 정보 
     */
    private CustomerDto customer;

    /**
     * 결제 완료 후 다시 서버로 전달되는 검증용 커스텀 데이터
     */
    private T customData;
}
