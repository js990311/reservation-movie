package com.rejs.reservation.domain.theater.controller;

import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class TheaterController {
    private final TheaterService theaterService;

    @PostMapping
    public BaseResponse<TheaterDto> createTheater(@RequestBody TheaterCreateRequest request){
        TheaterDto theater = theaterService.createTheater(request);
        return BaseResponse.of(201, theater);
    }
}
