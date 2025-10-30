package com.rejs.reservation.domain.screening.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import jdk.jfr.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureMockMvc
@SpringBootTest
class ScreeningControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @BeforeEach
    void setUp(){
        String movieName = "some-movie";
        Integer duration = 106;

        Movie movie = Movie.builder()
                .title(movieName)
                .duration(duration)
                .build();
        movie = movieRepository.save(movie);
        movieId = movie.getId();

        String name = "theater-name";
        Integer rowSize = 10;
        Integer colSize = 10;

        Theater theater = Theater.create(name, rowSize, colSize);
        theater = theaterRepository.save(theater);
        theaterId = theater.getId();
        startTime = LocalDateTime.now();
        endTime = startTime.plusMinutes(duration);
    }

    @Test
    @DisplayName("create screening - 201")
    void createScreening() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", theaterId,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.screeningId").isNumber())
                .andExpect(jsonPath("$.data.theaterId").value(theaterId))
                .andExpect(jsonPath("$.data.movieId").value(movieId))
                .andExpect(jsonPath("$.data.startTime").value(startTime))
                .andExpect(jsonPath("$.data.endTime").value(endTime))
        ;
    }
}