package com.rejs.reservation.domain.theater.service;

import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.TheaterSummaryDto;
import com.rejs.reservation.domain.theater.dto.TheaterWithSeatDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.entity.Seat;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.exception.TheaterExceptionCode;
import com.rejs.reservation.domain.theater.repository.SeatJdbcRepository;
import com.rejs.reservation.domain.theater.repository.SeatRepository;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.global.exception.BusinessException;
import io.micrometer.observation.annotation.Observed;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Observed
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;
    private final SeatJdbcRepository seatJdbcRepository;
    private final SeatRepository seatRepository;

    @Transactional
    @WithSpan("create.theater")
    public TheaterDto createTheater(TheaterCreateRequest theaterCreateRequest){
        Theater theater = Theater.create(theaterCreateRequest.getName(), theaterCreateRequest.getRowSize(), theaterCreateRequest.getColSize());
        theater = theaterRepository.save(theater);
        List<Seat> seats = new ArrayList<>();
        for(int row = 1; row <= theaterCreateRequest.getRowSize();row++){
            for(int col = 1; col <= theaterCreateRequest.getColSize();col++){
                seats.add(Seat.create(theater, row, col));
            }
        }
        seatRepository.saveAll(seats);
        return TheaterDto.from(theater);
    }

    public TheaterWithSeatDto readById(Long id) {
        Theater theater = theaterRepository.findById(id).orElseThrow(() -> BusinessException.of(TheaterExceptionCode.THEATER_NOT_FOUND, id + " THEATER NOT FOUND"));
        List<Seat> seats = seatRepository.findByTheater(theater);
        return TheaterWithSeatDto.from(theater,seats);
    }

    public Page<TheaterSummaryDto> readTheaters(Pageable pageable) {
        return theaterRepository.findAll(pageable).map(TheaterSummaryDto::from);
    }
}
