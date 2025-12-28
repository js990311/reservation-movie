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

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelCrudService {
    private final PaymentCancelRepository paymentCancelRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentCancelDto getOrCreate(String paymentId, PaymentCancelReason reason){
        PaymentCancel paymentCancel = paymentCancelRepository.findByPaymentUid(paymentId).orElseGet(() -> {
            Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
            PaymentCancel newPaymentCancel = new PaymentCancel(payment.optionalReservationId(), payment.getPaymentUid(), reason);
            return paymentCancelRepository.save(newPaymentCancel);
        });
        return PaymentCancelDto.from(paymentCancel);
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

}
