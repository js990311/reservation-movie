package com.rejs.reservation.domain.screening.exception;

import com.rejs.reservation.global.exception.GlobalBaseException;
import org.springframework.http.HttpStatus;

/**
 * 영화 상영시간표상 충돌 발생시
 */
public class DuplicationScreeningTimeException extends GlobalBaseException {
    public DuplicationScreeningTimeException() {
        super(HttpStatus.CONFLICT);
    }
}
