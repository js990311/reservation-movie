package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentInfo;
import com.rejs.reservation.domain.payments.repository.PaymentInfoQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentInfoService {
    private final PaymentInfoQueryRepository paymentInfoQueryRepository;

    public Page<PaymentInfo> getMyPayment(Long username, Pageable pageable){
        return paymentInfoQueryRepository.getMyPayment(username, pageable);
    }

    public Page<PaymentInfo> getAdminPayment(){
        return null;
    }

    public Page<PaymentInfo> getAdminPaymentCancel(){
        return null;
    }
}
