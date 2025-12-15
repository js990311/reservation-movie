package com.rejs.reservation.domain.reservation.service;

import com.rejs.reservation.domain.payments.service.PaymentCancelService;
import com.rejs.reservation.domain.reservation.dto.ReservationDetailDto;
import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSeatNumberDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.ReservationFacade;
import com.rejs.reservation.domain.screening.entity.Screening;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ReservationService {
    private final ReservationFacade reservationFacade;
    private final ScreeningRepository screeningRepository;
    private final PaymentCancelService paymentCancelService;

    // CREATE - Reservation
    @Transactional
    public ReservationDto reservationScreening(ReservationRequest request, Long userId){
        // 비관적 락
        Screening screening = screeningRepository.findWithLock(request.getScreeningId());

        // 좌석이 실제로 존재하는 지 가져오기
        // + 이미 예약된 좌석인지 검사하기
        List<Long> availableSeats = reservationFacade.selectAvailableSeats(
                request.getSeats(),
                screening.getTheaterId(),
                screening.getId()
        );

        if(availableSeats.size() != request.getSeats().size()){
            throw new BusinessException(ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS);
        }

        // reservation 및 reservationSeat 생성
        Reservation reservation = Reservation.create(userId, screening.getId(), availableSeats);
        reservation = reservationFacade.save(reservation);
        return ReservationDto.from(reservation);
    }

    // READ
    public Page<ReservationSummaryDto> findMyReservations(long userId, Pageable pageable) {
        return reservationFacade.findMyReservations(userId, pageable);
    }


    public ReservationDetailDto findById(Long id) {
        ReservationSummaryDto reservation = reservationFacade.findReservationSummaryById(id);
        List<ReservationSeatNumberDto> seats = reservationFacade.findSeatNumberById(id);
        return new ReservationDetailDto(reservation, seats);
    }

    // UPDATE

    // DELETE
    @Transactional
    public void deleteReservationByReservationId(Long reservationId){
        // reservation 찾아옴
        Reservation reservation = reservationFacade.findById(reservationId);

        // 성공한 결제내역을 찾아서 있으면
        paymentCancelService.cancelPayment(reservationId);

        // reservation을 취소처리함
        reservation.cancel();
    }

}

