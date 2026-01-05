package com.rejs.reservation.domain.theater.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.movie.dto.MovieDto;
import com.rejs.reservation.domain.movie.dto.request.MovieCreateRequest;
import com.rejs.reservation.domain.movie.service.MovieService;
import com.rejs.reservation.domain.screening.controller.docs.ScreeningDtoDocs;
import com.rejs.reservation.domain.screening.dto.request.CreateScreeningRequest;
import com.rejs.reservation.domain.screening.service.ScreeningService;
import com.rejs.reservation.domain.theater.controller.docs.CreateTheaterRequestDocs;
import com.rejs.reservation.domain.theater.controller.docs.TheaterDtoDocs;
import com.rejs.reservation.domain.theater.controller.docs.TheaterSummaryDocs;
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
import com.rejs.reservation.global.dto.response.BaseResponse;
import com.rejs.reservation.global.security.jwt.token.Tokens;
import com.rejs.reservation.global.security.jwt.utils.JwtUtils;
import com.rejs.reservation.global.security.service.LoginService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TheaterControllerTest extends AbstractControllerTest {
    @Autowired
    private TheaterRepository theaterRepository;

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

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setToken(){
        User user = new User(UUID.randomUUID().toString(), "pw", UserRole.ROLE_ADMIN);
        user = userRepository.save(user);
        UserDto userDto = UserDto.of(user);
        Tokens tokens = jwtUtils.generateToken(
                String.valueOf(userDto.getUserId()),
                Collections.singletonList(user.getRole().name())
        );
        accessToken = tokens.getAccessToken();
    }


    @Test
    @DisplayName("POST theaters - 201")
    void createTheater() throws Exception {
        String name = "theater-name";
        Integer rowSize = 10;
        Integer colSize = 10;
        Map<String, Object> request = Map.of(
                "name", name,
                "rowSize", rowSize,
                "colSize", colSize
        );

        ResultActions result = mockMvc.perform(post("/theaters")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + accessToken)
                .content(objectMapper.writeValueAsString(request))
        );

        result
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.theaterId").isNumber())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.rowSize").isNumber())
                .andExpect(jsonPath("$.data.colSize").isNumber())
        ;

        result
                .andDo(
                        document((docs) -> docs
                                .requestHeaders(authorizationHeader())
                                .requestSchema(CreateTheaterRequestDocs.schema())
                                .requestFields(CreateTheaterRequestDocs.fields())
                                .responseSchema(TheaterDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(TheaterDtoDocs.fields()))
                        )
                );
    }

    @Test
    @DisplayName("GEt theatersById - 200 ")
    void readTheaterById() throws Exception{
        String name = "theater-name";
        Integer rowSize = 10;
        Integer colSize = 10;

        TheaterDto theater = theaterService.createTheater(new TheaterCreateRequest(name, rowSize, colSize));
        Long id = theater.getTheaterId();

        ResultActions result = mockMvc.perform(get("/theaters/{id}", id)                        .header("Authorization", "Bearer " + accessToken)
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.theaterId").value(id))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.rowSize").isNumber())
                .andExpect(jsonPath("$.data.colSize").isNumber())
                .andExpect(jsonPath("$.data.seats").isArray())
                .andExpect(jsonPath("$.data.seats[0].seatId").isNumber())
                .andExpect(jsonPath("$.data.seats[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data.seats[0].row").isNumber())
                .andExpect(jsonPath("$.data.seats[0].col").isNumber())
        ;

        result
                .andDo(
                        document((docs) -> docs
                                .requestHeaders(authorizationHeader())
                                .pathParameters(parameterWithName("id").description("영화관 id"))
                                .responseSchema(TheaterDtoDocs.theaterWithSeatsSchema())
                                .responseFields(BaseResponseDocs.baseFields(TheaterDtoDocs.theaterWithSeats()))
                        )
                );
    }

    @Test
    @DisplayName("GEt theatersById - 404")
    void readTheaterById_404() throws Exception{
        ResultActions result = mockMvc.perform(get("/theaters/{id}", 0)
                .header("Authorization", "Bearer " + accessToken)
        );

        andExpectException(()->result,TheaterExceptionCode.THEATER_NOT_FOUND , "/theaters/0");

        result
                .andDo(
                        documentWithException((docs) -> docs
                                .requestHeaders(authorizationHeader())
                                .pathParameters(parameterWithName("id").description("영화관 id"))
                        )
                );
    }

    @Test
    void readTheaters() throws Exception{
        int count = 20;
        for(int i=1;i<=count;i++){
            theaterService.createTheater(new TheaterCreateRequest(
                    UUID.randomUUID().toString(),
                    30,
                    30
            ));
        }

        ResultActions result = mockMvc.perform(get("/theaters")
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", "0")
                .queryParam("size", "10")
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].theaterId").isNumber())
                .andExpect(jsonPath("$.data[0].name").isString())
                .andExpect(jsonPath("$.data[0].rowSize").isNumber())
                .andExpect(jsonPath("$.data[0].colSize").isNumber())

                .andExpect(jsonPath("$.pagination.count").isNumber())
                .andExpect(jsonPath("$.pagination.requestNumber").isNumber())
                .andExpect(jsonPath("$.pagination.requestSize").isNumber())
                .andExpect(jsonPath("$.pagination.hasNextPage").isBoolean())
                .andExpect(jsonPath("$.pagination.totalPage").isNumber())
                .andExpect(jsonPath("$.pagination.totalElements").isNumber())
        ;

        result
                .andDo(
                        document(docs->docs
                                .requestHeaders(authorizationHeader())
                                .queryParameters(
                                        parameterWithName("page").description("요청한 페이지번호"),
                                        parameterWithName("size").description("페이지 내부의 데이터 개수")
                                )
                                .responseFields(
                                        BaseResponseDocs.withPaginations(TheaterSummaryDocs.fields())
                                )
                        )
                )
        ;
    }


}