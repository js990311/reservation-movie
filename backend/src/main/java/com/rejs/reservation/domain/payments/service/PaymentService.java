package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    @Transactional
    public PaymentLockResult startVerification(String paymentId) {
        try {
            Optional<Payment> opt = paymentRepository.findByPaymentUid(paymentId);
            if (opt.isEmpty()) {
                return PaymentLockResult.NOT_FOUND;
            }

            // 이미 완료된건지 체크
            Payment payment = opt.get();
            if (payment.isCompleted()) {
                return PaymentLockResult.ALREADY_COMPLETED;
            }

            int isLocked = paymentRepository.tryToUpdate(paymentId);

            if (isLocked == 0) {
                log.warn("[payment.lock.conflict] 원자적 연산 실패로 인한 실패 paymentId={}", paymentId);
                return PaymentLockResult.ALREADY_COMPLETED;
            }

            return PaymentLockResult.LOCKED;

        }catch (CannotAcquireLockException ex){
            log.warn("[payment.lock.conflict] DB 경합 발생 - 다른 프로세스가 선점함 paymentId={}", paymentId, ex);
            throw ex; // 여기서 예외를 처리해도 밖에서 UnexpectedRollbackException이 터지므로
        }
    }

    @Transactional(readOnly = true)
    public void validatePayment(String paymentId, Long totalAmount){
        // readOnly라 멱등성이 어긋날리가 없음
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));
        Reservation reservation = payment.getReservation();
        // 결제 금액이 맞는 지 검증
        if(reservation.getTotalAmount().longValue() != totalAmount){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    @Transactional
    public PaymentInfoDto confirmReservation(Long reservationId, String paymentId){
        Reservation reservation = reservationRepository.findWithLockById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));

        // 이미 상태가 변화되었으면 상태변화 메서드 호출 없이 그대로
        if(reservation.getStatus().equals(ReservationStatus.CONFIRMED) || payment.getStatus().equals(PaymentStatus.PAID)){
            return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
        }
        reservation.confirm();
        payment.paid();
        return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
    }

    @Transactional
    public void abortPayment(String paymentId){
        log.info("abortPayment: tx active={}, name={}, isolation={}, readOnly={}",
                TransactionSynchronizationManager.isActualTransactionActive(),
                TransactionSynchronizationManager.getCurrentTransactionName(),
                TransactionSynchronizationManager.getCurrentTransactionIsolationLevel(),
                TransactionSynchronizationManager.isCurrentTransactionReadOnly()
        );
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseGet(() -> {
            // NOT found 케이스에 대한 방어를 위해
            Payment newPayment = Payment.notFoundPayment(paymentId);
            return paymentRepository.saveAndFlush(newPayment); // 후속 쿼리에서 감지를 못하는 문제 발생
        });
        payment.aborted();
    }

    @Transactional(readOnly = true)
    public PaymentInfoDto getPaymentInfo(String paymentId){
        Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        return PaymentInfoDto.from(payment);
    }
}
