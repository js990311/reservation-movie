package com.rejs.reservation.domain.payments.controller;

import com.rejs.reservation.domain.payments.dto.CompletePaymentRequest;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.service.PaymentLogService;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentLogService paymentLogService;

    @GetMapping
    public BaseResponse<List<PaymentLogDto>> getPayments(
            @PageableDefault Pageable pageable
    ){
        return BaseResponse.ofPage(paymentLogService.findByPagination(pageable));
    }

    @PostMapping("/complete")
    public BaseResponse<PaymentLogDto> completePayment(@RequestBody CompletePaymentRequest request){
        PaymentLogDto paymentLogDto = paymentService.syncPayment(request.getPaymentId());
        return BaseResponse.of(paymentLogDto);
    }
}
