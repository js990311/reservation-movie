package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.domain.screening.service.ScreeningService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/screenings")
public class ScreeningController {
    private final ScreeningService screeningService;
}
