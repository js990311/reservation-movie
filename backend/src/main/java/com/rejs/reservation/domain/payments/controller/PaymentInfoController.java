package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.PaymentInfo;
import com.rejs.reservation.domain.payments.service.PaymentInfoService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.security.jwt.resolver.TokenClaim;
import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentInfoController {
    private final PaymentInfoService paymentInfoService;

    @GetMapping("/me")
    public BaseResponse<List<PaymentInfo>> getMyPayment(
            @PageableDefault Pageable pageable,
            @TokenClaim ClaimsDto claimsDto
    ){
        Long username = Long.parseLong(claimsDto.getUsername());
        return BaseResponse.ofPage(paymentInfoService.getMyPayment(username, pageable));
    }
}
