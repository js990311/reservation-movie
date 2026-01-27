package com.rejs.reservation.domain.reservation.controller;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.reservation.controller.docs.ReservationDetailDtoDocs;
import com.rejs.reservation.domain.reservation.controller.docs.ReservationDtoDocs;
import com.rejs.reservation.domain.reservation.controller.docs.ReservationRequestDocs;
import com.rejs.reservation.domain.reservation.controller.docs.ReservationSummaryDtoDocs;
import com.rejs.reservation.domain.reservation.dto.ReservationDto;
import com.rejs.reservation.domain.reservation.dto.request.ReservationRequest;
import com.rejs.reservation.domain.reservation.entity.Reservation;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.reservation.repository.jpa.ReservationRepository;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.entity.ScreeningSeat;
import com.rejs.reservation.domain.screening.repository.ScreeningSeatRepository;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.SeatDto;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.LoginResponse;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.exception.BusinessException;
import com.rejs.reservation.global.exception.code.BusinessExceptionCode;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class ReservationControllerTest extends AbstractControllerTest {
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

    @Autowired
    private JwtUtils jwtUtils;

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ScreeningSeatRepository screeningSeatRepository;

    private Long movieId;
    private Long theaterId;
    private Long screeningId;
    private Long userId;

    private List<Long> seatIds;
    private List<ScreeningSeat> seats;

    @BeforeEach
    void setup(){
        LoginResponse loginResponse = loginService.signup(new LoginRequest(UUID.randomUUID().toString(), "pw"));
        accessToken = loginResponse.getTokens().getAccessToken().getToken();

        // 영화 생성
        MovieCreateRequest movieRequest = new MovieCreateRequest("title", 145);
        MovieDto movie = movieService.createMovie(movieRequest);
        movieId = movie.getMovieId();

        // 영화관 생성
        TheaterCreateRequest theaterRequest = new TheaterCreateRequest("theater1", 30, 30);
        TheaterDto theater = theaterService.createTheater(theaterRequest);
        theaterId = theater.getTheaterId();

        entityManager.flush();

        // 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theater.getTheaterId(), movie.getMovieId(), LocalDateTime.now().plusDays(3));
        ScreeningDto screening = screeningService.createScreening(screeningRequest);
        screeningId = screening.getScreeningId();

        User user = new User("username", "password");
        user = userRepository.save(user);
        userId = user.getId();

        entityManager.flush();

        seats = screeningSeatRepository.findByScreeningId(screeningId);
        seatIds = seats.stream().map(ScreeningSeat::getId).toList();
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

        result
                .andDo(document(
                        docs -> docs
                                .requestSchema(ReservationRequestDocs.schema())
                                .requestFields(ReservationRequestDocs.fields())
                                .responseSchema(ReservationDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(ReservationDtoDocs.fields()))
                ));

    }
    @Test
    void 일부좌석이예약() throws Exception{
        // 예약이 진행되었다고 가정
        int reservationSeatCount = 5;
        Reservation reservation = Reservation.create(userId, screeningId, seats);
        reservationRepository.save(reservation);

        entityManager.flush();

        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode expectCode = ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS;
        andExpectException(()->result, ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS, "/reservations");

        result
                .andDo(documentWithException(
                        docs -> docs
                                .requestSchema(ReservationRequestDocs.schema())
                                .requestFields(ReservationRequestDocs.fields())
                ));

    }

    @Test
    void 모든좌석예약() throws Exception{
        // 예약이 진행되었다고 가정
        Reservation reservation = Reservation.create(userId, screeningId, seats);
        reservationRepository.save(reservation);

        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        ResultActions result = mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode expectCode = ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS;
        andExpectException(()->result, ReservationExceptionCode.INVALID_OR_UNAVAILABLE_SEATS, "/reservations");

        result
                .andDo(documentWithException(
                        docs -> docs
                                .requestSchema(ReservationRequestDocs.schema())
                                .requestFields(ReservationRequestDocs.fields())
                ));
    }

    @Test
    void 다른상영시간의영향을받는지() throws Exception{
        // 다른 상영표 생성
        CreateScreeningRequest screeningRequest = new CreateScreeningRequest(theaterId, movieId, LocalDateTime.now().plus(999, ChronoUnit.MINUTES));
        ScreeningDto screening2 = screeningService.createScreening(screeningRequest);
        List<Long> seatIds2 = screeningSeatRepository.findByScreeningId(screening2.getScreeningId())
                .stream().map(ScreeningSeat::getId).toList();
        // 기존 상영표의 예약이 모두 완료되었다고 가정
        Reservation reservation = Reservation.create(userId, screeningId, seats);
        reservationRepository.save(reservation);

        entityManager.flush();

        ReservationRequest reservationRequest = new ReservationRequest(screening2.getScreeningId(), seatIds2);
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

        result
                .andDo(document(
                        docs -> docs
                                .requestSchema(ReservationRequestDocs.schema())
                                .requestFields(ReservationRequestDocs.fields())
                                .responseSchema(ReservationDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(ReservationDtoDocs.fields()))
                ));
    }

    @Test
    void 나의예매보기() throws Exception{
        ReservationRequest reservationRequest = new ReservationRequest(screeningId, seatIds);

        mockMvc.perform(
                post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest))
                        .header("Authorization", "Bearer " + accessToken)
        );

        ResultActions result = mockMvc.perform(
                get("/reservations/me")
                        .queryParam("page", "0")
                        .queryParam("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].reservationId").isNumber())
                .andExpect(jsonPath("$.data[0].status").isString())

                .andExpect(jsonPath("$.data[0].screeningId").isNumber())
                .andExpect(jsonPath("$.data[0].startTime").isString())
                .andExpect(jsonPath("$.data[0].endTime").isString())

                .andExpect(jsonPath("$.data[0].movieId").isNumber())
                .andExpect(jsonPath("$.data[0].movieTitle").isString())

                .andExpect(jsonPath("$.data[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data[0].theaterName").isString())

                .andExpect(jsonPath("$.pagination.count").isNumber())
                .andExpect(jsonPath("$.pagination.requestNumber").isNumber())
                .andExpect(jsonPath("$.pagination.requestSize").isNumber())
                .andExpect(jsonPath("$.pagination.hasNextPage").isBoolean())
                .andExpect(jsonPath("$.pagination.totalPage").isNumber())
                .andExpect(jsonPath("$.pagination.totalElements").isNumber())
        ;

        result
                .andDo(document(
                        docs -> docs
                                .requestHeaders(authorizationHeader())
                                .queryParameters(
                                        parameterWithName("page").description("요청한 페이지번호"),
                                        parameterWithName("size").description("페이지 내부의 데이터 개수")
                                )
                                .responseSchema(ReservationSummaryDtoDocs.schema())
                                .responseFields(BaseResponseDocs.withPaginations(ReservationSummaryDtoDocs.fields()))
                ));
    }

    @Test
    void 남의예매정보보기() throws Exception{
        // 예매하기
        int reservationSeatCount = 5;
        Reservation reservation = Reservation.create(userId, screeningId, seats.subList(0, reservationSeatCount));
        reservation = reservationRepository.save(reservation);

        ResultActions result = mockMvc.perform(
                get("/reservations/{id}", reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode code = ReservationExceptionCode.NOT_RESERVATION_OWNER;
        andExpectException(()->result, code, "/reservations/"+reservation.getId());

        result
                .andDo(documentWithException(
                        docs -> docs
                                .pathParameters(
                                        parameterWithName("id").description("예매 ID")
                                )
                                .requestHeaders(authorizationHeader())
                ));
    }

    @Test
    void 예매정보보기() throws Exception{
        // 예매하기
        int reservationSeatCount = 5;
        Long reservationUserId = Long.valueOf(jwtUtils.getClaims(accessToken).getUsername());
        Reservation reservation = Reservation.create(reservationUserId, screeningId, seats.subList(0, reservationSeatCount));
        reservation = reservationRepository.save(reservation);

        ResultActions result = mockMvc.perform(
                get("/reservations/{id}", reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservation.reservationId").isNumber())
                .andExpect(jsonPath("$.data.reservation.status").isString())

                .andExpect(jsonPath("$.data.reservation.screeningId").isNumber())
                .andExpect(jsonPath("$.data.reservation.startTime").isString())
                .andExpect(jsonPath("$.data.reservation.endTime").isString())

                .andExpect(jsonPath("$.data.reservation.movieId").isNumber())
                .andExpect(jsonPath("$.data.reservation.movieTitle").isString())

                .andExpect(jsonPath("$.data.reservation.theaterId").isNumber())
                .andExpect(jsonPath("$.data.reservation.theaterName").isString())

                .andExpect(jsonPath("$.data.seats[0].row").isNumber())
                .andExpect(jsonPath("$.data.seats[0].col").isNumber())
        ;

        result
                .andDo(document(
                        docs -> docs
                                .pathParameters(
                                        parameterWithName("id").description("예매 ID")
                                )
                                .requestHeaders(authorizationHeader())
                                .responseSchema(ReservationDetailDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(ReservationDetailDtoDocs.fields()))
                ));

    }

    @Test
    void 없는예매정보보기() throws Exception{
        ResultActions result = mockMvc.perform(
                get("/reservations/{id}", 0)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + accessToken)
        );

        BusinessExceptionCode code = ReservationExceptionCode.RESERVATION_NOT_FOUND;
        andExpectException(()->result, code, "/reservations/0");

        result
                .andDo(documentWithException(
                        docs -> docs
                                .pathParameters(
                                        parameterWithName("id").description("예매 ID")
                                )
                                .requestHeaders(authorizationHeader())
                ));
    }

}