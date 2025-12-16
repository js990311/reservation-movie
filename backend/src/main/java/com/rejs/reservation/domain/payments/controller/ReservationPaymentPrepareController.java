package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.PaymentPrepareDto;
import com.rejs.reservation.domain.payments.service.PaymentPrepareService;
import com.rejs.reservation.domain.reservation.authorizer.annotation.IsReservationOwner;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reservations/{id}/payments/prepare")
public class ReservationPaymentPrepareController {
    private final PaymentPrepareService paymentPrepareService;

    @IsReservationOwner
    @PostMapping
    public BaseResponse<PaymentPrepareDto> getReservationPrepare(@PathVariable("id") long id){
        return BaseResponse.of(paymentPrepareService.prepare(id));
    }
}
