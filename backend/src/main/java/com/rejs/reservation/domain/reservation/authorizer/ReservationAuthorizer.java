package com.rejs.reservation.domain.reservation.authorizer;

import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.security.jwt.token.ClaimsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component("reservationAuthorizer")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationAuthorizer {
    private final ReservationRepository reservationRepository;

    public boolean check(Long reservationId, Authentication authentication){
        Optional<Reservation> opt = reservationRepository.findById(reservationId);
        if(opt.isEmpty()){
            throw BusinessException.of(ReservationExceptionCode.RESERVATION_NOT_FOUND);
        }
        Object principal = authentication.getPrincipal();
        if(principal instanceof ClaimsDto claims){
            Reservation reservation = opt.get();
            if(reservation.getUserId().equals(Long.parseLong(claims.getUsername()))){
                return true;
            }else {
                throw BusinessException.of(ReservationExceptionCode.NOT_RESERVATION_OWNER);
            }
        }
        throw BusinessException.of(ReservationExceptionCode.NOT_RESERVATION_OWNER);
    }
}
