package com.rejs.reservation.domain.theater.controller;

import com.rejs.reservation.domain.theater.service.TheaterService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/theaters")
public class TheaterController {
    private final TheaterService theaterService;
}
