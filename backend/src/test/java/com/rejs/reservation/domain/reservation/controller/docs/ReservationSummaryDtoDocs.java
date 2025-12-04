package com.rejs.reservation.domain.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReservationSummaryDtoDocs {
    public static Schema schema(){
        return new Schema("reservationSummary");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("상영표 id"),
                fieldWithPath("status").type(JsonFieldType.STRING).description("예매 상태"),
                fieldWithPath("screeningId").description("상영표 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("startTime").description("영화시작시간").type(JsonFieldType.STRING),
                fieldWithPath("endTime").description("영화종료시간").type(JsonFieldType.STRING),
                fieldWithPath("movieId").description("영화 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("movieTitle").description("영화제목").type(JsonFieldType.STRING),
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("theaterName").description("영화관이름").type(JsonFieldType.STRING)
        );
    }
}
