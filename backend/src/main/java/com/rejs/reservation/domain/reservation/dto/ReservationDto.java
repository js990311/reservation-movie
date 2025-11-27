package com.rejs.reservation.domain.reservation.dto;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class ReservationDto {
    private Long reservationId;
    private ReservationStatus status;
    private Long userId;
    private Long screeningId;
    private List<ReservationSeatDto> reservationSeats;

    public static ReservationDto from(Reservation reservation){
        return ReservationDto.builder()
                .reservationId(reservation.getId())
                .status(reservation.getStatus())
                .userId(reservation.getUserId())
                .screeningId(reservation.getScreeningId())
                .reservationSeats(reservation.getReservationSeats().stream().map(ReservationSeatDto::from).toList())
                .build();
    }
}
