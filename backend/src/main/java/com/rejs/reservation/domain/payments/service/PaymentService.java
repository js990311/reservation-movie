package com.rejs.reservation.domain.payments.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentLogDto;
import com.rejs.reservation.domain.payments.entity.PaymentLog;
import com.rejs.reservation.domain.payments.entity.PaymentStatus;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import io.portone.sdk.server.common.Currency;
import io.portone.sdk.server.common.SelectedChannelType;
import io.portone.sdk.server.payment.PaidPayment;
import io.portone.sdk.server.payment.Payment;
import io.portone.sdk.server.payment.PaymentClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;


@RequiredArgsConstructor
@Service
public class PaymentService {
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public PaymentLogDto syncPayment(String paymentId) {
        PaymentLog paymentLog = paymentRepository.findByPaymentId(paymentId).orElseGet(() -> new PaymentLog(paymentId));
        try {
            // payment client에서 payment 가져오기
            Payment payment;
            try {
                CompletableFuture<Payment> future = paymentClient.getPayment(paymentId);
                payment = future.join();
            }catch (CompletionException e){
                throw e;
            }catch (Exception e){
                throw e;
            }

            if(payment instanceof PaidPayment paidPayment){
                boolean verified = verifyPayment(paidPayment);
                if(verified){
                    String customDataJson = paidPayment.getCustomData();
                    CustomDataDto customData = objectMapper.readValue(customDataJson, CustomDataDto.class);
                    Long reservationId = customData.getReservationId();
                    Reservation reservation = reservationRepository.findById(reservationId).orElseThrow();
                    reservation.confirm();

                    paymentLog.success();
                    paymentLog.mapReservaiton(reservation);
                    paymentRepository.save(paymentLog);
                    return PaymentLogDto.from(paymentLog);
                }
            }
        }catch (Exception e){
            paymentLog.failed();
            paymentRepository.save(paymentLog);
            return PaymentLogDto.from(paymentLog);
        }
        return new PaymentLogDto(paymentId, PaymentStatus.FAILED, null);
    }


    @Transactional(readOnly = true)
    public boolean verifyPayment(PaidPayment paidPayment) {
        // LIVE가 아닌 이유는 TEST로 실행하고 있으니까
        if(!paidPayment.getChannel().getType().equals(SelectedChannelType.Test.INSTANCE)){
            return false;
        }
        // 커스텀 데이터가 존재하지 않으면 검증 실패
        String customDataJson = paidPayment.getCustomData();
        if(customDataJson == null){
            return false;
        }
        CustomDataDto customData;
        try {
            customData = objectMapper.readValue(customDataJson, CustomDataDto.class);
        }catch (JsonProcessingException e){
            return false;
        }
        // 결제가 원화로 이루어지지 않았으면 실패
        if(!paidPayment.getCurrency().equals(Currency.Krw.INSTANCE)){
            return false;
        }
        Long reservationId = customData.getReservationId();
        long totalAmount = paidPayment.getAmount().getTotal();
        Optional<Reservation> opt = reservationRepository.findById(reservationId);
        if(opt.isEmpty()){
            return false;
        }
        Reservation reservation = opt.get();
        return reservation.getTotalAmount().longValue() == totalAmount;
    }
}
