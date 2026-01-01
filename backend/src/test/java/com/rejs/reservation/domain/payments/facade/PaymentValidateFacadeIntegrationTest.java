package com.rejs.reservation.domain.payments.facade;

import com.rejs.reservation.TestcontainersConfiguration;
import com.rejs.reservation.domain.payments.adapter.PortOneAdaptor;
import com.rejs.reservation.domain.payments.adapter.dto.PaymentStatusDto;
import com.rejs.reservation.domain.payments.dto.CustomDataDto;
import com.rejs.reservation.domain.payments.dto.PaymentInfoDto;
import com.rejs.reservation.domain.payments.entity.payment.Payment;
import com.rejs.reservation.domain.payments.entity.payment.PaymentStatus;
import com.rejs.reservation.domain.payments.exception.PaymentExceptionCode;
import com.rejs.reservation.domain.payments.exception.PaymentValidateException;
import com.rejs.reservation.domain.payments.repository.PaymentRepository;
import com.rejs.reservation.domain.payments.service.PaymentLockResult;
import com.rejs.reservation.domain.payments.service.PaymentService;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.screening.entity.ScreeningSeatStatus;
import com.rejs.reservation.domain.theater.entity.Seat;
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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@Import(TestcontainersConfiguration.class)
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

    private List<ScreeningSeat> seats;

    @BeforeEach
    void setUp() {
        // 프로젝트 내의 모든 객체를... 다 생성할 순 없으므로
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        // 결제 실패에 대한 테스트가 아니기 때문에 전부 성공한다고
        when(portOneAdaptor.cancelPayment(anyString(), any())).thenReturn(CompletableFuture.completedFuture(true));

        Seat mockSeat = mock(Seat.class);
        lenient().when(mockSeat.getId()).thenReturn(1L);

        seats =List.of(
                new ScreeningSeat(1L, 10000, mockSeat, null, ScreeningSeatStatus.AVAILABLE),
                new ScreeningSeat(2L, 10000, mockSeat, null, ScreeningSeatStatus.AVAILABLE),
                new ScreeningSeat(3L, 10000, mockSeat, null, ScreeningSeatStatus.AVAILABLE)
        );
    }


    @AfterEach
    void cleanUp() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    @Test
    @DisplayName("결제 성공시나리오")
    void validate() {
        // 초기데이터 삽입
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
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(CompletableFuture.completedFuture(paymentStatus));

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
    @DisplayName("결제 검증 통합 테스트")
    void validateAlreadyCompleted() throws InterruptedException {
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
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(CompletableFuture.completedFuture(paymentStatus));

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        CyclicBarrier barrier = new CyclicBarrier(threadCount);
        for (int i=0; i<threadCount;i++){
            executorService.submit(()-> {
                try {
                    barrier.await();
                    PaymentInfoDto validate = paymentValidateFacade.validate(paymentId);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executorService.shutdown();

        // spybean이라서 실제 실행 횟수를 확인할 수 있음
        verify(portOneAdaptor, times(1)).getPayment(paymentId);
        verify(paymentService, times(1)).validateAndConfirm(reservation.getId(), paymentId, amount);
    }

    @Test
    @DisplayName("존재하지 않는 결제 시나리오")
    void paymentNotFound() {
        // 서버쪽와 사전 합의되지 않은 내역이 들어옴
        String paymentId = "123456";

        // 환불해야함
        assertThrows(PaymentValidateException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        Payment payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.ABORTED, payment.getStatus());
    }

    @Test
    @DisplayName("외부 API로 결제정보 취득 실패 시나리오")
    void validateGetPaymentInfoFail(){
        // 초기데이터 삽입
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 비정상적으로 작동 = 결제 시도 정보 획득에 실패
        when(portOneAdaptor.getPayment(paymentId)).thenThrow(new PaymentValidateException(PaymentExceptionCode.PAYMENT_API_ERROR));

        // w
        assertThrows(PaymentValidateException.class,()->paymentValidateFacade.validate(paymentId));

        // t
        // 상태 확인
        payment = paymentRepository.findByPaymentUid(paymentId).orElseThrow();
        assertEquals(PaymentStatus.ABORTED, payment.getStatus());

        reservation = reservationRepository.findById(reservation.getId()).orElseThrow();
        assertEquals(ReservationStatus.PENDING, reservation.getStatus());
    }

    @Test
    @DisplayName("결제 메타데이터 실패 시나리오")
    void validatePaymentStatusValidationFail(){
        // 초기데이터 삽입
        Reservation reservation = Reservation.create(1L, 2L, seats);
        reservation = reservationRepository.save(reservation);
        Payment payment = Payment.create(reservation);
        payment = paymentRepository.save(payment);

        String paymentId = payment.getPaymentUid();
        Long amount = Long.valueOf(reservation.getTotalAmount());

        // 외부 API가 응답할 데이터
        PaymentStatusDto paymentStatus = mock(PaymentStatusDto.class);

        // 외부 API가 정상적으로 작동
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(CompletableFuture.completedFuture(paymentStatus));

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
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(CompletableFuture.completedFuture(paymentStatus));

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
        when(portOneAdaptor.getPayment(paymentId)).thenReturn(CompletableFuture.completedFuture(paymentStatus));

        // 모든 검증이 성공했는데 트랜잭션에서 실패
        doThrow(new RuntimeException()).when(paymentService).validateAndConfirm(customDataDto.getReservationId(), paymentId, amount);

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
