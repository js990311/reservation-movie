package com.rejs.reservation.domain.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.service.LoginService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieService movieService;

    @Autowired
    private ScreeningService screeningService;

    @Autowired
    private TheaterService theaterService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private EntityManager entityManager;

    private Long movieId;
    private Long theaterId;
    private Long screeningId;
    private Long userId;

    private List<Long> seatIds;

    @BeforeEach
    void setup(){
        Tokens tokens = loginService.signup(new LoginRequest(UUID.randomUUID().toString(), "pw"));
        accessToken = tokens.getAccessToken();

        // 영화 생성
        MovieCreateRequest movieRequest = new MovieCreateRequest("title", 145);
        MovieDto movie = movieService.createMovie(movieRequest);
        movieId = movie.getMovieId();

        // 영화관 생성
        TheaterCreateRequest theaterRequest = new TheaterCreateRequest("theater1", 30, 30);
        TheaterDto theater = theaterService.createTheater(theaterRequest);
        theaterId = theater.getTheaterId();

        // 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now());
        ScreeningDto screening = screeningService.createScreening(screeningRequest);
        screeningId = screening.getScreeningId();

        seatIds = theater.getSeats().stream().map(SeatDto::getSeatId).collect(Collectors.toList());

        User user = new User("username", "password");
        user = userRepository.save(user);
        userId = user.getId();

    }

    @Test
    void reservation() throws Exception{
        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reservationId").isNumber())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.userId").isNumber())
                .andExpect(jsonPath("$.data.screeningId").isNumber())
                .andExpect(jsonPath("$.data.screeningId").value(screeningId))
                .andExpect(jsonPath("$.data.reservationSeats").isArray())
        ;
    }
    @Test
    void 일부좌석이예약() throws Exception{
        // 예약이 진행되었다고 가정
        int reservationSeatCount = 5;
        Reservation reservation = Reservation.create(userId, screeningId, seatIds.subList(0, reservationSeatCount));
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode expectCode = ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS;
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(expectCode.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(expectCode.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(expectCode.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/reservations"))
                .andExpect(jsonPath("$.detail").isString());

    }

    @Test
    void 모든좌석예약() throws Exception{
        // 예약이 진행되었다고 가정
        Reservation reservation = Reservation.create(userId, screeningId, seatIds);
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode expectCode = ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS;
        result
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").isString())
                .andExpect(jsonPath("$.type").value(expectCode.getType()))
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.title").value(expectCode.getTitle()))
                .andExpect(jsonPath("$.status").isNumber())
                .andExpect(jsonPath("$.status").value(expectCode.getStatus().value()))
                .andExpect(jsonPath("$.instance").isString())
                .andExpect(jsonPath("$.instance").value("/reservations"))
                .andExpect(jsonPath("$.detail").isString());
    }

    @Test
    void 다른상영시간의영향을받는지() throws Exception{
        // 다른 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theaterId, movieId, LocalDateTime.now().plus(999, ChronoUnit.MINUTES));
        ScreeningDto screening2 = screeningService.createScreening(screeningRequest);

        // 기존 상영표의 예약이 모두 완료되었다고 가정
        Reservation reservation = Reservation.create(userId, screeningId, seatIds);
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = new ReservationRequest(screening2.getScreeningId(), seatIds);
        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.reservationId").isNumber())
                .andExpect(jsonPath("$.data.status").isString())
                .andExpect(jsonPath("$.data.userId").isNumber())
                .andExpect(jsonPath("$.data.screeningId").isNumber())
                .andExpect(jsonPath("$.data.screeningId").value(screening2.getScreeningId()))
                .andExpect(jsonPath("$.data.reservationSeats").isArray())
        ;
    }

}