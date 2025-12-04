package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.domain.screening.dto.ScreeningDetailDto;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/screenings")
public class ScreeningController {
    private final ScreeningService screeningService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BaseResponse<ScreeningDto> createScreening(@RequestBody CreateScreeningRequest request){
        ScreeningDto screening = screeningService.createScreening(request);
        return BaseResponse.of(screening);
    }

    @GetMapping("/{id}")
    public BaseResponse<ScreeningDetailDto> getScreening(
            @PathVariable("id") Long id
    ){
        ScreeningDetailDto screening = screeningService.readScreeningById(id);
        return BaseResponse.of(screening);
    }
}
