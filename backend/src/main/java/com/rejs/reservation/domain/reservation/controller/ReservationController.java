package com.rejs.reservation.domain.reservation.controller;

import com.rejs.reservation.domain.reservation.dto.ReservationDetailDto;
import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.ReservationSummaryDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.service.ReservationService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.security.jwt.resolver.TokenClaim;
import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseResponse<ReservationDto> reservation(@RequestBody ReservationRequest request, @TokenClaim ClaimsDto claimsDto){
        long userId = Long.parseLong(claimsDto.getUsername());
        return BaseResponse.of(reservationService.reservationScreening(request, userId));
    }

    @GetMapping("/me")
    public BaseResponse<List<ReservationSummaryDto>> getMyReservation(
            @PageableDefault Pageable pageable,
            @TokenClaim ClaimsDto claimsDto
    ){
        long userId = Long.parseLong(claimsDto.getUsername());
        Page<ReservationSummaryDto> reservations = reservationService.findMyReservations(userId, pageable);
        return BaseResponse.ofPage(reservations);
    }

    @GetMapping("/{id}")
    public BaseResponse<ReservationDetailDto> getReservationById(@PathVariable("id") Long id, @TokenClaim ClaimsDto claimsDto){
        long userId = Long.parseLong(claimsDto.getUsername());
        ReservationDetailDto reservation = reservationService.findById(id, userId);
        return BaseResponse.of(reservation);
    }


}
