package com.rejs.reservation.domain.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Test
    @DisplayName("POST /movie - 200")
    void createMovie() throws Exception{
        String movieName = "some-movie";
        Integer duration = 106;

        Map<String, Object> request = Map.of(
                "title", movieName,
                "duration", duration
        );

        ResultActions result = mockMvc.perform(
                post("/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        result
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.movieId").isNumber())
                .andExpect(jsonPath("$.data.title").value(movieName))
                .andExpect(jsonPath("$.data.duration").value(duration))
        ;
    }

    @Test
    @DisplayName("GET /movie/:id - 200")
    void getMovieById() throws Exception{

        String movieName = "some-movie";
        Integer duration = 106;

        Movie movie = Movie.builder()
                .title(movieName)
                .duration(duration)
                .build();
        movie = movieRepository.save(movie);
        Long id = movie.getId();

        Map<String, Object> request = Map.of(
                "title", movieName,
                "duration", duration
        );

        ResultActions result = mockMvc.perform(
                get("/movies/{id}", id)
        );

        result
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.movieId").value(id))
                .andExpect(jsonPath("$.data.title").value(movieName))
                .andExpect(jsonPath("$.data.duration").value(duration))
        ;
    }

    @Test
    @DisplayName("GET /movie/:id - 404")
    void getMovieById_404() throws Exception{

        String movieName = "some-movie";
        Integer duration = 106;

        Movie movie = Movie.builder()
                .title(movieName)
                .duration(duration)
                .build();
        movie = movieRepository.save(movie);
        Long id = 0L;

        Map<String, Object> request = Map.of(
                "title", movieName,
                "duration", duration
        );

        ResultActions result = mockMvc.perform(
                get("/movies/{id}", id)
        );

        result
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
    }


}