package com.rejs.reservation.domain.movie.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.service.LoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup(){
        Tokens tokens = loginService.signup(new LoginRequest("id", "pw"));
        accessToken = tokens.getAccessToken();
    }

    @AfterEach
    void clear(){
        userRepository.deleteAll();;
    }

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
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isCreated())
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
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
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
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(MovieBusinessExceptionCode.MOVIE_NOT_FOUND.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(MovieBusinessExceptionCode.MOVIE_NOT_FOUND.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(MovieBusinessExceptionCode.MOVIE_NOT_FOUND.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/movies/" + id))
                .andExpect(jsonPath("$.detail").isString())
        ;
    }


}