package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.CompletePaymentRequest;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/complete")
    public BaseResponse<PaymentLogDto> completePayment(@RequestBody CompletePaymentRequest request){
        PaymentLogDto paymentLogDto = paymentService.syncPayment(request.getPaymentId());
        return BaseResponse.of(paymentLogDto);
    }
}
