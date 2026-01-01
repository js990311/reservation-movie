package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentCancelDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelCrudService {
    private final PaymentCancelRepository paymentCancelRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public Optional<PaymentCancelDto> tryLockForCancel(String paymentId, PaymentCancelReason reason){
        LocalDateTime now = LocalDateTime.now();
        // 생성은 외부에서 다 했어야함
        int updatedCount = paymentCancelRepository.updateLastAttemptedAt(paymentId, now, now.minusMinutes(3L));

        if (updatedCount > 0) {
            // 내가 선점에 성공했거나, 처음 시도하는 경우
            return Optional.of(paymentCancelRepository.findByPaymentUid(paymentId)
                    .map(PaymentCancelDto::from)
                    .orElseThrow());
        }
        return Optional.empty();
    }

    // update
    @Transactional
    public void canceled(Long paymentCancelId){
        PaymentCancel paymentCancel = paymentCancelRepository.findById(paymentCancelId).orElseThrow();
        paymentCancel.canceled();
    }

    @Transactional
    public void failed(Long paymentCancelId){
        PaymentCancel paymentCancel = paymentCancelRepository.findById(paymentCancelId).orElseThrow();
        paymentCancel.failed();
    }

    public void skipped(Long paymentCancelId) {
        PaymentCancel paymentCancel = paymentCancelRepository.findById(paymentCancelId).orElseThrow();
        paymentCancel.skipped();
    }
}
