package com.rejs.reservation.domain.screening.controller;

import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningDtoDocs;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningWithMovieDtoDocs;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningWithTheaterDtoDocs;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.LoginResponse;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.service.LoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScreeningMappingControllerTest extends AbstractControllerTest {

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private MovieService movieService;

    @BeforeEach
    void setToken(){
        LoginResponse loginResponse = loginService.signup(new LoginRequest(UUID.randomUUID().toString(), "pw"));
        accessToken = loginResponse.getTokens().getAccessToken().getToken();
    }

    @AfterEach
    void clearToken(){
        userRepository.deleteAll();;
    }

    @Test
    void readTheaterScreeningDateNull() throws Exception{
        TheaterDto theater = theaterService.createTheater(new TheaterCreateRequest(
                UUID.randomUUID().toString(),
                30,
                30
        ));
        MovieDto movie = movieService.createMovie(new MovieCreateRequest("movie", 1));

        int count = 20;
        LocalDateTime now = LocalDateTime.now();
        for(int i=1;i<=count;i++){
            screeningService.createScreening(new CreateScreeningRequest(
                    theater.getTheaterId(),
                    movie.getMovieId(),
                    now
            ));
            now = now.plusMinutes(10);
        }

        ResultActions result = mockMvc.perform(get("/theaters/{id}/screenings", theater.getTheaterId())
                .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].screeningId").isNumber())
                .andExpect(jsonPath("$.data[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data[0].startTime").isString())
                .andExpect(jsonPath("$.data[0].endTime").isString())
                .andExpect(jsonPath("$.data[0].movieId").isNumber())
                .andExpect(jsonPath("$.data[0].title").isString())
                .andExpect(jsonPath("$.data[0].duration").isNumber())
                .andExpect(jsonPath("$.pagination.count").isNumber())
        ;

        result
                .andDo(
                        document(docs->docs
                                .requestHeaders(authorizationHeader())
                                .responseFields(
                                        BaseResponseDocs.withList(ScreeningWithMovieDtoDocs.fields())
                                )
                        )
                )
        ;
    }

    @Test
    void readTheaterScreeningTodayDate() throws Exception{
        TheaterDto theater = theaterService.createTheater(new TheaterCreateRequest(
                UUID.randomUUID().toString(),
                30,
                30
        ));
        MovieDto movie = movieService.createMovie(new MovieCreateRequest("movie", 1));

        int count = 20;
        LocalDateTime now = LocalDateTime.now();
        for(int i=1;i<=count;i++){
            screeningService.createScreening(new CreateScreeningRequest(
                    theater.getTheaterId(),
                    movie.getMovieId(),
                    now
            ));
            now = now.plusMinutes(10);
        }

        LocalDate date = LocalDate.now();

        ResultActions result = mockMvc.perform(get("/theaters/{id}/screenings", theater.getTheaterId())
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("date", date.format(DateTimeFormatter.ISO_DATE))
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].screeningId").isNumber())
                .andExpect(jsonPath("$.data[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data[0].startTime").isString())
                .andExpect(jsonPath("$.data[0].endTime").isString())
                .andExpect(jsonPath("$.data[0].movieId").isNumber())
                .andExpect(jsonPath("$.data[0].title").isString())
                .andExpect(jsonPath("$.data[0].duration").isNumber())
                .andExpect(jsonPath("$.pagination.count").isNumber())
        ;

        result
                .andDo(
                        document(docs->docs
                                .requestHeaders(authorizationHeader())
                                .queryParameters(
                                        parameterWithName("date").description("예매할 날짜")
                                )
                                .responseFields(
                                        BaseResponseDocs.withList(ScreeningWithMovieDtoDocs.fields())
                                )
                        )
                )
        ;
    }


    @Test
    void readMoviesScreening() throws Exception{
        TheaterDto theater = theaterService.createTheater(new TheaterCreateRequest(
                UUID.randomUUID().toString(),
                30,
                30
        ));
        MovieDto movie = movieService.createMovie(new MovieCreateRequest("movie", 1));

        int count = 20;
        LocalDateTime now = LocalDateTime.now();
        for(int i=1;i<=count;i++){
            screeningService.createScreening(new CreateScreeningRequest(
                    theater.getTheaterId(),
                    movie.getMovieId(),
                    now
            ));
            now = now.plusMinutes(10);
        }

        LocalDate date = LocalDate.now();

        ResultActions result = mockMvc.perform(get("/movies/{id}/screenings", movie.getMovieId())
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("date", date.format(DateTimeFormatter.ISO_DATE))
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].screeningId").isNumber())
                .andExpect(jsonPath("$.data[0].movieId").isNumber())
                .andExpect(jsonPath("$.data[0].startTime").isString())
                .andExpect(jsonPath("$.data[0].endTime").isString())
                .andExpect(jsonPath("$.data[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data[0].theaterName").isString())

                .andExpect(jsonPath("$.pagination.count").isNumber())
        ;

        result
                .andDo(
                        document(docs->docs
                                .requestHeaders(authorizationHeader())
                                .queryParameters(
                                        parameterWithName("date").description("예매할 날짜")
                                )
                                .responseFields(
                                        BaseResponseDocs.withList(ScreeningWithTheaterDtoDocs.fields())
                                )
                        )
                )
        ;

    }
}