package com.rejs.reservation.domain.screening.service;

import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;
}
