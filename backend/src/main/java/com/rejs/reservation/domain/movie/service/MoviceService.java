package com.rejs.reservation.domain.movie.service;

import com.rejs.reservation.domain.movie.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MoviceService {
    private final MovieRepository movieRepository;
}
