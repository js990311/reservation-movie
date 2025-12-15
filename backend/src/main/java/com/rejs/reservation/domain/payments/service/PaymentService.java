package com.rejs.reservation.domain.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.entity.cancel.PaymentCancel;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentCancelRepository;
import com.rejs.reservation.domain.payments.repository.PaymentLogRepository;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.payments.repository.ReservationPaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentStateService paymentStateService;
    private final PaymentLogRepository paymentLogRepository;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;
    private final PaymentCancelService paymentCancelService;
    private final ReservationPaymentRepository reservationPaymentRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    // 결제 검증 관련

    @Transactional(readOnly = true)
    public PaymentLogDto syncPayment(String paymentId) {
        // 멱등성 보장
        Optional<PaymentLog> opt = paymentLogRepository.findByPaymentId(paymentId);
        if(opt.isPresent()){
            PaymentLog paymentLog = opt.get();
            if(paymentLog.getStatus().equals(PaymentStatus.PAID)){
                return PaymentLogDto.from(paymentLog);
            }
        }
        Long reservationId = null;
        try {
            // payment client에서 payment 가져오기
            Payment payment = fetchPayment(paymentId);

            // payment가 적절한 상태인지 검증
            PaidPayment paidPayment = verifyPayment(payment);

            // 커스텀 데이터 추출 및 정보 검증
            CustomDataDto customData = this.extractCustomData(paidPayment);
            reservationId = customData.getReservationId();
            Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(()->new BusinessException(PaymentExceptionCode.RESERVATION_NOT_FOUND));

            // 중복 결제인지 검사
            if(reservationPaymentRepository.existsPaymentByReservationId(reservation.getId())){
                throw new BusinessException(PaymentExceptionCode.ALREADY_PAID_RESERVATION);
            }

            // 금액 비교
            long totalAmount = paidPayment.getAmount().getTotal();
            if(reservation.getTotalAmount().longValue() != totalAmount){
                throw new BusinessException(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
            }

            // 성공시 처리
            PaymentLog paymentLog = paymentStateService.successPaymentLog(paymentId, reservationId);
            return PaymentLogDto.from(paymentLog);
        }catch (BusinessException e){
            paymentStateService.failPaymentLog(paymentId, reservationId, e.getMessage());
            paymentCancelService.cancelPayment(paymentId, "결제 검증 실패");
            throw e;
        }catch (Exception e){
            paymentStateService.failPaymentLog(paymentId, reservationId, e.getMessage());
            paymentCancelService.cancelPayment(paymentId, "결제 검증 실패");
            throw new BusinessException(PaymentExceptionCode.PAYMENT_VALIDATION_FAIL);
        }
    }

    public Payment fetchPayment(String paymentId){
        try {
            CompletableFuture<Payment> future = paymentClient.getPayment(paymentId);
            return future.join();
        }catch (Exception e) {
            throw new BusinessException(PaymentExceptionCode.PAYMENT_API_ERROR, "결제 서버로부터 결제정보를 가져오지 못했습니다.");
        }
    }

    public PaidPayment verifyPayment(Payment payment){
        if(payment instanceof PaidPayment paidPayment){
            // 결제환경이 TEST인지 확인(포트폴리오는 TEST로만 진행함)
            if(!paidPayment.getChannel().getType().equals(SelectedChannelType.Test.INSTANCE)){
                throw new BusinessException(PaymentExceptionCode.INVALID_CHANNEL);
            }

            // KRW로 결제가 되었는지 검사
            if(!paidPayment.getCurrency().equals(Currency.Krw.INSTANCE)){
                throw new BusinessException(PaymentExceptionCode.INVALID_CURRENCY);
            }

            return paidPayment;
        }else {
            throw new BusinessException(PaymentExceptionCode.INVALID_PAYMENT_STATE);
        }
    }

    public CustomDataDto extractCustomData(PaidPayment paidPayment){
        String customDataJson = paidPayment.getCustomData();
        if(customDataJson == null){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        CustomDataDto customData;
        try {
            customData = objectMapper.readValue(customDataJson, CustomDataDto.class);
        }catch (JsonProcessingException e){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }catch (Exception e){
            throw new BusinessException(PaymentExceptionCode.MISSING_CUSTOM_DATA);
        }
        return customData;
    }

    /**
     * 데이터베이스의 데이터와 검증
     * @param reservationId
     * @param totalAmount
     */
    @Transactional(readOnly = true)
    public void validatePayment(Long reservationId, Long totalAmount){
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        // 결제 금액이 맞는 지 검증
        if(reservation.getTotalAmount().longValue() != totalAmount){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }

    @Transactional(readOnly = true)
    public void validatePayment(String paymentId, Long totalAmount){
        // readOnly라 멱등성이 어긋날리가 없음
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));
        Reservation reservation = reservationRepository.findById(payment.getReservationId()).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        // 결제 금액이 맞는 지 검증
        if(reservation.getTotalAmount().longValue() != totalAmount){
            throw BusinessException.of(PaymentExceptionCode.PAYMENT_AMOUNT_MISMATCH);
        }
    }


    @Transactional
    public PaymentLogDto confirmReservation(Long reservationId, String paymentId){
        Reservation reservation = reservationRepository.findWithLockById(reservationId).orElseThrow(() -> BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND));
        com.rejs.reservation.domain.payments.entity.payment.Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow(() -> BusinessException.of(PaymentExceptionCode.PAYMENT_NOT_FOUND));

        // 이미 상태가 변화되었으면 상태변화 메서드 호출 없이 그대로
        if(reservation.getStatus().equals(ReservationStatus.CONFIRMED) || payment.getStatus().equals(PaymentStatus.PAID)){
            return new PaymentLogDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
        }
        reservation.confirm();
        payment.paid();
        return new PaymentLogDto(payment.getPaymentUid(), payment.getStatus(), reservation.getId());
    }

}
