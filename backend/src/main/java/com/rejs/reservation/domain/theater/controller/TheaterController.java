package com.rejs.reservation.domain.theater.controller;

import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{id}")
    public BaseResponse<TheaterDto> readTheaterById(@PathVariable("id") Long id){
        TheaterDto theaterDto = theaterService.readById(id);
        return BaseResponse.of(200, theaterDto);
    }
}
