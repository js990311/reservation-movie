package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.CompletePaymentRequest;
import com.rejs.reservation.domain.payments.dto.ValidatePaymentInfoDto;
import com.rejs.reservation.domain.payments.facade.PaymentValidateFacade;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentValidateFacade paymentValidateFacade;

    @PostMapping("/complete")
    public BaseResponse<ValidatePaymentInfoDto> completePayment(@RequestBody CompletePaymentRequest request){
        ValidatePaymentInfoDto payment = paymentValidateFacade.validate(request.getPaymentId());
        return BaseResponse.of(payment);
    }
}
