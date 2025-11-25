package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.global.dto.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

}
