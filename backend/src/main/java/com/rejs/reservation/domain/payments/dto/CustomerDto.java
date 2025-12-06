package com.rejs.reservation.domain.payments.dto;

import lombok.Getter;

@Getter
public class CustomerDto {
    /**
     * 구매자 고유아이디
     */
    private String customerId;
    /**
     * 구매자 이메일 주소
     */
    private String email;
}
