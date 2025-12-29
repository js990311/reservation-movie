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

    /**
     * 불필요하게 나뉜 기존의 검증 트랜잭션과 승인 트랜잭션을 하나로 합침
     * @param reservationId
     * @param paymentId
     * @return
     */
    @Transactional
    public PaymentInfoDto validateAndConfirm(Long reservationId, String paymentId, Long totalAmount){
        // 스케줄러 등 상태를 변경시키는 race condition으로부터 안전을 위해서 비관적 락 설정
        Reservation reservation = reservationRepository.findWithLockById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));

        // 결제 정보 불러오기
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));

        // 혹시라도 payment가 다른 예매에 대한 결제가 아닌 지 검증(reservationId를 custom data에서 가져왔기 때문)
        if(!payment.getReservation().getId().equals(reservation.getId())){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_INFO_MISMATCH);
        }

        // 결제 총 금액과의 비교
        if(reservation.getTotalAmount().longValue() != totalAmount){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
        }

        // 이미 상태가 변화되었으면 상태변화 메서드 호출 없이 그대로
        if(reservation.getStatus().equals(ReservationStatus.CONFIRMED) || payment.getStatus().equals(PaymentStatus.PAID)){
            return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
        }

        // 스케쥴러 등으로 인해 결제보다 취소가 빨랐으면 비즈니스 로직상 결제 취소 후 환불
        if(reservation.getStatus().equals(ReservationStatus.CANCELED)){
            throw BusinessException.of(PaymentExceptionCode.RESERVATION_ALREADY_CANCELED);
        }

        reservation.confirm();
        payment.paid();
        return new PaymentInfoDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
    }

    @Transactional
    public void abortPayment(String paymentId){
        log.info("cancelPayment: tx active={}, name={}, isolation={}, readOnly={}",
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
