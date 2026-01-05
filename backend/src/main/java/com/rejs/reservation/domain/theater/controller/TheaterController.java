package com.rejs.reservation.domain.theater.controller;

import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.TheaterSummaryDto;
import com.rejs.reservation.domain.theater.dto.TheaterWithSeatDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class TheaterController {
    private final TheaterService theaterService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseResponse<TheaterDto> createTheater(@RequestBody TheaterCreateRequest request){
        TheaterDto theater = theaterService.createTheater(request);
        return BaseResponse.of(theater);
    }

    @GetMapping("/{id}")
    public BaseResponse<TheaterWithSeatDto> readTheaterById(@PathVariable("id") Long id){
        TheaterWithSeatDto theaterDto = theaterService.readById(id);
        return BaseResponse.of(theaterDto);
    }

    @GetMapping
    public BaseResponse<List<TheaterSummaryDto>> readTheaters(@PageableDefault Pageable pageable){
        Page<TheaterSummaryDto> theaters = theaterService.readTheaters(pageable);
        return BaseResponse.ofPage(theaters);
    }
}
