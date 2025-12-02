package com.rejs.reservation.domain.screening.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ScreeningWithMovieDtoDocs {
    public static Schema schema(){
        return new Schema("screening");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("screeningId").description("상영표 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("startTime").description("영화시작시간").type(JsonFieldType.STRING),
                fieldWithPath("endTime").description("영화종료시간").type(JsonFieldType.STRING),
                fieldWithPath("movieId").description("영화 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("title").description("영화제목").type(JsonFieldType.STRING),
                fieldWithPath("duration").description("상영시간").type(JsonFieldType.NUMBER)
        );
    }

}
