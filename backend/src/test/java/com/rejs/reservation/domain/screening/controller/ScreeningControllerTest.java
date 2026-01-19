package com.rejs.reservation.domain.screening.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.reservation.exception.ReservationExceptionCode;
import com.rejs.reservation.domain.screening.controller.docs.CreateScreeningRequestDocs;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningDetailDtoDocs;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningDtoDocs;
import com.rejs.reservation.domain.screening.dto.ScreeningDto;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.exception.ScreeningExceptionCode;
import com.rejs.reservation.domain.screening.repository.ScreeningRepository;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.dto.TheaterDto;
import com.rejs.reservation.domain.theater.dto.request.TheaterCreateRequest;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.exception.TheaterExceptionCode;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import com.rejs.reservation.domain.theater.service.TheaterService;
import com.rejs.reservation.domain.user.dto.UserDto;
import com.rejs.reservation.domain.user.dto.request.LoginRequest;
import com.rejs.reservation.domain.user.entity.User;
import com.rejs.reservation.domain.user.entity.UserRole;
import com.rejs.reservation.domain.user.repository.UserRepository;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import com.rejs.reservation.global.security.service.LoginService;
import jakarta.persistence.EntityManager;
import jdk.jfr.ContentType;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class ScreeningControllerTest extends AbstractControllerTest {
    @Autowired
    private ScreeningRepository screeningRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreeningService screeningService;


    @Autowired
    private TheaterService theaterService;

    private Long movieId;
    private Long theaterId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer duration = 120;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void setToken(){
        User user = new User(UUID.randomUUID().toString(), "pw", UserRole.ROLE_ADMIN);
        user = userRepository.save(user);
        UserDto userDto = UserDto.of(user);
        Tokens tokens = jwtUtils.generateToken(
                String.valueOf(userDto.getUserId()),
                Collections.singletonList(user.getRole().name())
        );
        accessToken = tokens.getAccessToken().getToken();
    }

    @BeforeEach
    void setUp(){
        Movie movie = Movie.builder()
                .title("some-movie")
                .duration(duration)
                .build();
        movie = movieRepository.save(movie);
        movieId = movie.getId();

        TheaterDto theaterDto = theaterService.createTheater(
                new TheaterCreateRequest("theater-name", 10, 10)
        );
        theaterId = theaterDto.getTheaterId();

        startTime = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        endTime = startTime.plusMinutes(duration);

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("create screening - 201")
    void createScreening() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", theaterId,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(
                post("/screenings").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer " + accessToken)

        );

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.screeningId").isNumber())
                .andExpect(jsonPath("$.data.theaterId").value(theaterId))
                .andExpect(jsonPath("$.data.movieId").value(movieId))
                .andExpect(jsonPath("$.data.startTime").value(startTime.format(formatter)))
                .andExpect(jsonPath("$.data.endTime").value(endTime.format(formatter)))
        ;

        result
                .andDo(document((docs)->docs
                        .requestHeaders(authorizationHeader())
                        .requestSchema(CreateScreeningRequestDocs.schema())
                        .requestFields(CreateScreeningRequestDocs.fields())
                        .responseSchema(ScreeningDtoDocs.schema())
                        .responseFields(BaseResponseDocs.baseFields(ScreeningDtoDocs.fields()))
                ));
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
        mockMvc.perform(post("/screenings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        // 겹치게 시간표 생성
        startTime = startTime.plusMinutes(duration / 2);
        request = Map.of(
                "theaterId", theaterId,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        andExpectException(()->result, ScreeningExceptionCode.SCREENING_TIME_CONFLICT, "/screenings");

        result
                .andDo(
                        documentWithException(docs->docs
                                .requestHeaders(authorizationHeader())
                                .requestSchema(CreateScreeningRequestDocs.schema())
                                .requestFields(CreateScreeningRequestDocs.fields())
                        )
                );
    }

    @Test
    @DisplayName("createScreeningInvalidTheaterId - 404")
    void createScreeningInvalidTheaterId() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", 0,
                "movieId", movieId,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        andExpectException(()->result, TheaterExceptionCode.THEATER_NOT_FOUND, "/screenings");

        result
                .andDo(
                        documentWithException(docs->docs
                                .requestHeaders(authorizationHeader())
                                .requestSchema(CreateScreeningRequestDocs.schema())
                                .requestFields(CreateScreeningRequestDocs.fields())
                        )
                );
    }

    @Test
    @DisplayName("createScreeningInvalidMovieId - 404")
    void createScreeningInvalidMovieId() throws Exception {
        Map<String, Object> request = Map.of(
                "theaterId", theaterId,
                "movieId", 0,
                "startTime", startTime
        );

        ResultActions result = mockMvc.perform(post("/screenings")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(request)));

        andExpectException(()->result, MovieBusinessExceptionCode.MOVIE_NOT_FOUND, "/screenings");
        result
                .andDo(
                        documentWithException(docs->docs
                                .requestHeaders(authorizationHeader())
                                .requestSchema(CreateScreeningRequestDocs.schema())
                                .requestFields(CreateScreeningRequestDocs.fields())
                        )
                );
    }

    @Test
    void getScreeningById() throws Exception{
        ScreeningDto screening = screeningService.createScreening(new CreateScreeningRequest(theaterId, movieId, LocalDateTime.now()));
        ResultActions result = mockMvc.perform(
                get("/screenings/{id}", screening.getScreeningId())
                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.screening.screeningId").isNumber())
                .andExpect(jsonPath("$.data.screening.screeningId").value(screening.getScreeningId()))
                .andExpect(jsonPath("$.data.screening.movieId").isNumber())
                .andExpect(jsonPath("$.data.screening.movieId").value(screening.getMovieId()))
                .andExpect(jsonPath("$.data.screening.theaterId").isNumber())
                .andExpect(jsonPath("$.data.screening.theaterId").value(screening.getTheaterId()))
                .andExpect(jsonPath("$.data.screening.startTime").isString())
                .andExpect(jsonPath("$.data.screening.endTime").isString())
                .andExpect(jsonPath("$.data.movie.movieId").isNumber())
                .andExpect(jsonPath("$.data.movie.title").isString())
                .andExpect(jsonPath("$.data.movie.duration").isNumber())
                .andExpect(jsonPath("$.data.theater.theaterId").isNumber())
                .andExpect(jsonPath("$.data.theater.name").isString())
                .andExpect(jsonPath("$.data.theater.rowSize").isNumber())
                .andExpect(jsonPath("$.data.theater.colSize").isNumber())
                .andExpect(jsonPath("$.data.seats").isArray())
                .andExpect(jsonPath("$.data.seats[0].seatId").isNumber())
                .andExpect(jsonPath("$.data.seats[0].row").isNumber())
                .andExpect(jsonPath("$.data.seats[0].col").isNumber())
                .andExpect(jsonPath("$.data.seats[0].reserved").isBoolean())
        ;

        result.andDo(
                document(docs->docs
                    .pathParameters(
                        parameterWithName("id").description("screening 고유번호")
                    )
                        .requestHeaders(authorizationHeader())
                        .responseSchema(ScreeningDetailDtoDocs.getSchema())
                        .responseFields(BaseResponseDocs.baseFields(ScreeningDetailDtoDocs.getFields()))
                )
        );

    }

    @Test
    void getScreening404() throws Exception{
        ResultActions result = mockMvc.perform(get("/screenings/{id}", 0)
                .header("Authorization", "Bearer " + accessToken)
        );
        ScreeningExceptionCode code = ScreeningExceptionCode.SCREENING_NOT_FOUND;

        andExpectException(()->result, ScreeningExceptionCode.SCREENING_NOT_FOUND, "/screenings/0");

        result.andDo(
                documentWithException(docs->docs
                        .pathParameters(
                        parameterWithName("id").description("screening 고유번호")
                        )
                        .requestHeaders(authorizationHeader())
                )
        );
    }

}