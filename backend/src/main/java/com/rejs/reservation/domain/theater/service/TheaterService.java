package com.rejs.reservation.domain.theater.service;

import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TheaterService {
    private final TheaterRepository theaterRepository;

    @Transactional
    public TheaterDto createTheater(TheaterCreateRequest theaterCreateRequest){
        Theater theater = Theater.create(theaterCreateRequest.getName(), theaterCreateRequest.getRowSize(), theaterCreateRequest.getColSize());
        theater = theaterRepository.save(theater);
        return TheaterDto.from(theater);
    }

}
