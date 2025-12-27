package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
@Transactional
@ExtendWith(MockitoExtension.class)
@SpringBootTest
class PaymentValidateFacadeIntegrationTest {
    @MockitoBean
    private PortOneAdaptor portOneAdaptor;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockitoSpyBean
    private PaymentService paymentService;

    @Autowired
    private PaymentValidateFacade paymentValidateFacade;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 프로젝트 내의 모든 객체를... 다 생성할 순 없으므로
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
    }

    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    @Test
    @DisplayName("결제 성공시나리오")
    void validate() {
        // 초기데이터 삽입
        List<Long> seats =  List.of(1L,2L,3L);
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 응답할 데이터
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(reservation.getId());
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);

        // w : 검증 실시
        paymentValidateFacade.validate(paymentId);

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.PAID, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.CONFIRMED, reservation.getStatus());
    }

    @Test
    @DisplayName("외부 API로 결제정보 취득 실패 시나리오")
    void validateGetPaymentInfoFail(){
        // 초기데이터 삽입
        List<Long> seats =  List.of(1L,2L,3L);
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 비정상적으로 작동 = 결제 시도 정보 획득에 실패
        when(portOneAdaptor.getPayment(paymentId)).thenThrow(BusinessException.of(PaymentExceptionCode.PAYMENT_API_ERROR));

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.READY, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    @DisplayName("결제 메타데이터 실패 시나리오")
    void validatePaymentStatusValidationFail(){
        // 초기데이터 삽입
        List<Long> seats =  List.of(1L,2L,3L);
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 응답할 데이터
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);

        // paymentStatus에서의 검증 실패 시나리오
        doThrow(BusinessException.of(PaymentExceptionCode.INVALID_CHANNEL)).when(paymentStatus).validate();

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.ABORTED, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    @DisplayName("결제 금액 검증 실패 시나리오")
    void validatePaymentFail(){
        // 초기데이터 삽입
        List<Long> seats =  List.of(1L,2L,3L);
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = 0L; // 사용자가 0원

        // 외부 API가 응답할 데이터
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(reservation.getId());
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);

        // w
        assertThrows(BusinessException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.ABORTED, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    @DisplayName("결제 승인 실패 시나리오")
    void validateConfirmFail(){
        // 초기데이터 삽입
        List<Long> seats =  List.of(1L,2L,3L);
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 응답할 데이터
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);
        CustomDataDto customDataDto = new CustomDataDto(reservation.getId());
        when(paymentStatus.getCustomData()).thenReturn(customDataDto);
        when(paymentStatus.getTotalAmount()).thenReturn(amount);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(paymentStatus);

        // 모든 검증이 성공했는데 트랜잭션에서 실패
        doThrow(new RuntimeException()).when(paymentService).confirmReservation(customDataDto.getReservationId(), paymentId);

        // w
        assertThrows(Exception.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.ABORTED, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

}
