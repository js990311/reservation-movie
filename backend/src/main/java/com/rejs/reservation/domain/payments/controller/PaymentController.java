package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.CompletePaymentRequest;
import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.facade.PaymentVaildateFacade;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentVaildateFacade paymentVaildateFacade;

    @PostMapping("/complete")
    public BaseResponse<PaymentInfoDto> completePayment(@RequestBody CompletePaymentRequest request){
        PaymentInfoDto payment = paymentVaildateFacade.validate(request.getPaymentId());
        return BaseResponse.of(payment);
    }
}
