package com.rejs.reservation.domain.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentLogRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CompletableFuture;


@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final PaymentStateService paymentStateService;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public PaymentLogDto syncPayment(String paymentId) {
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
            throw e;
        }catch (Exception e){
            paymentStateService.failPaymentLog(paymentId, reservationId, e.getMessage());
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
}
