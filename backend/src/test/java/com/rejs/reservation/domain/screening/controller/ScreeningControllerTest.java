package com.rejs.reservation.domain.screening.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.domain.theater.service.TheaterService;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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


    @Autowired
    private TheaterService theaterService;

    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration = 120;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @BeforeEach
    void setUp(){
        String movieName = "some-movie";

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
        startTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
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
                .andExpect(jsonPath("$.data.startTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.endTime").value(endTime.format(formatter)))
        ;
    }

    @Test
    @DisplayName("create screening duplication - 500")
    void createScreeningDuplication() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", theaterId,
                "movieId", movieId,
                "startTime", startTime
        );
        // 미리 시간표 생성
        mockMvc.perform(post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        // 겹치게 시간표 생성
        startTime = startTime.plusMinutes(duration / 2);
        request = Map.of(
                "theaterId", theaterId,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));
        result
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("createScreeningInvalidTheaterId - 400")
    void createScreeningInvalidTheaterId() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", 0,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

    @Test
    @DisplayName("createScreeningInvalidMovieId - 400")
    void createScreeningInvalidMovieId() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", theaterId,
                "movieId", 0,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }

}