package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.repository.PaymentLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentLogService {
    private final PaymentLogRepository paymentLogRepository;

    public Page<PaymentLogDto> findByPagination(Pageable pageable){
        return paymentLogRepository.findAll(pageable).map(PaymentLogDto::from);
    }
}
