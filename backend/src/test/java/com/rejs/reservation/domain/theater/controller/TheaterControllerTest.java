package com.rejs.reservation.domain.theater.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rejs.reservation.domain.theater.entity.Theater;
import com.rejs.reservation.domain.theater.repository.TheaterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class TheaterControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TheaterRepository theaterRepository;

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
                .content(objectMapper.writeValueAsString(request))
        );

        result
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.theaterId").isNumber())
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.seats.count").value(rowSize * colSize))
                .andExpect(jsonPath("$.data.seats[0].seatId").isNumber())
                .andExpect(jsonPath("$.data.seats[0].row").isNumber())
                .andExpect(jsonPath("$.data.seats[0].col").isNumber())
        ;
    }
}