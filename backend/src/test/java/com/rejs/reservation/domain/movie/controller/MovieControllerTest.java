package com.rejs.reservation.domain.movie.controller;

import com.epages.restdocs.apispec.ResourceDocumentation;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.controller.AbstractControllerTest;
import com.rejs.reservation.controller.docs.BaseResponseDocs;
import com.rejs.reservation.domain.movie.controller.docs.CreateMovieRequestDocs;
import com.rejs.reservation.domain.movie.controller.docs.MovieDtoDocs;
import com.rejs.reservation.domain.movie.entity.Movie;
import com.rejs.reservation.domain.movie.exception.MovieBusinessExceptionCode;
import com.rejs.reservation.domain.movie.repository.MovieRepository;
import com.rejs.reservation.domain.theater.controller.docs.TheaterDtoDocs;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.io.Serializable;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MovieControllerTest extends AbstractControllerTest {
    @Autowired
    private MovieRepository movieRepository;

    private String accessToken;

    @Autowired
    private LoginService loginService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @BeforeEach
    void setup(){
        User user = new User(UUID.randomUUID().toString(), "pw", UserRole.ROLE_ADMIN);
        user = userRepository.save(user);
        UserDto userDto = UserDto.of(user);
        Tokens tokens = jwtUtils.generateToken(
                String.valueOf(userDto.getUserId()),
                Collections.singletonList(user.getRole().name())
        );
        accessToken = tokens.getAccessToken().getToken();
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

        result.andDo(
                document((docs)->
                        docs
                                .requestSchema(CreateMovieRequestDocs.schema())
                                .requestFields(CreateMovieRequestDocs.fields())
                                .requestHeaders(authorizationHeader())
                                .responseSchema(MovieDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(MovieDtoDocs.fields()))
                )
        );
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

        result.andDo(
                document((docs)->
                        docs
                                .pathParameters(parameterWithName("id").description("영화 고유번호").description(JsonFieldType.NUMBER))
                                .requestHeaders(authorizationHeader())
                                .responseSchema(MovieDtoDocs.schema())
                                .responseFields(BaseResponseDocs.baseFields(MovieDtoDocs.fields()))
                )
        );
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

        ResultActions result = mockMvc.perform(
                get("/movies/{id}", id)
                        .header("Authorization", "Bearer " + accessToken)
        );

        andExpectException(()->result, MovieBusinessExceptionCode.MOVIE_NOT_FOUND, "/movies/" + id);

        result.andDo(
                documentWithException((docs)->
                        docs
                                .pathParameters(parameterWithName("id").description("영화 고유번호").description(JsonFieldType.NUMBER))
                                .requestHeaders(authorizationHeader())
                )
        );
    }

    @Test
    void getMovies() throws Exception{
        ResultActions result = mockMvc.perform(get("/movies")
                .header("Authorization", "Bearer " + accessToken)
                .queryParam("page", "0")
                .queryParam("size", "10")
        );

        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].movieId").isNumber())
                .andExpect(jsonPath("$.data[0].title").isString())
                .andExpect(jsonPath("$.data[0].duration").isNumber())

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
                                        ResourceDocumentation.parameterWithName("page").description("요청한 페이지번호"),
                                        ResourceDocumentation.parameterWithName("size").description("페이지 내부의 데이터 개수")
                                )
                                .responseFields(
                                        BaseResponseDocs.withPaginations(MovieDtoDocs.fields())
                                )
                        )
                )
        ;

    }
}