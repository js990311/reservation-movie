package com.rejs.reservation.domain.payments.service;

import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancelReason;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.exception.PaymentValidateException;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    /**
     * 너무 낮은 재시도 방지를 위해서
     * @param paymentId
     * @return 진입 가능시 true
     */
    @Transactional
    public boolean tryLockForVerification(String paymentId) {
        LocalDateTime now = LocalDateTime.now();
        // 생성은 외부에서 다 했어야함
        int updatedCount = paymentRepository.updateLastAttemptedAt(paymentId, now, now.minusSeconds(30L));

        if (updatedCount > 0) {
            return true;
        }else if (paymentRepository.existsByPaymentUid(paymentId)){
            return false;
        }else {
            throw new PaymentValidateException(PaymentExceptionCode.PAYMENT_NOT_FOUND);
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
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseGet(() -> {
            // NOT found 케이스에 대한 방어를 위해
            Payment newPayment = Payment.notFoundPayment(paymentId);
            return paymentRepository.saveAndFlush(newPayment); // 후속 쿼리에서 감지를 못하는 문제 발생
        });
        payment.aborted();

        // PaymentCancel의 생성을 이제 결제 거부할 때 같이하도록 지시
        PaymentCancel paymentCancel = new PaymentCancel(payment, PaymentCancelReason.VALIDATION_FAILED);
        paymentCancelRepository.save(paymentCancel);
    }

    @Transactional(readOnly = true)
    public PaymentInfoDto getPaymentInfo(String paymentId){
        Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        return PaymentInfoDto.from(payment);
    }

    @Transactional
    public boolean processZombiePayment(String paymentUid){
        LocalDateTime now = LocalDateTime.now();
        // 생성은 외부에서 다 했어야함
        int updatedCount = paymentRepository.updateForCleanUp(paymentUid, now, now.minusSeconds(30L));
        if(updatedCount == 0){
            return false;
        }

        // 서버와 합의되지 않은 결제 처리
        Optional<Payment> opt = paymentRepository.findByPaymentUid(paymentUid);
        if (opt.isEmpty()){
            Payment newPayment = Payment.notFoundPayment(paymentUid);
            paymentRepository.saveAndFlush(newPayment);
            PaymentCancel paymentCancel = new PaymentCancel(newPayment, PaymentCancelReason.VALIDATION_FAILED);
            paymentCancelRepository.save(paymentCancel);
            return true;
        }


        Payment payment = opt.get();
        if(payment.getStatus().equals(PaymentStatus.READY)){
            if(!payment.getReservation().getStatus().equals(ReservationStatus.PENDING)){
                // 아직 결제 시도가 이루어지지 않았는데 예매는 이미 대기상태가 아니다 -> 타임아웃
                payment.timeout();
                PaymentCancel paymentCancel = new PaymentCancel(payment, PaymentCancelReason.VALIDATION_FAILED);
                paymentCancelRepository.save(paymentCancel);
                return true;
            }
        }else if(payment.getStatus().equals(PaymentStatus.VERIFYING)){
            // 돈이 들어왔는데 검증에 실패했다 -> 결제 거부
            payment.aborted();
            PaymentCancel paymentCancel = new PaymentCancel(payment, PaymentCancelReason.VALIDATION_FAILED);
            paymentCancelRepository.save(paymentCancel);
            return true;
        }
        return false;
    }
}
