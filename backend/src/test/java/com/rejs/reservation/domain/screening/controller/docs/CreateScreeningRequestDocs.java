package com.rejs.reservation.domain.screening.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CreateScreeningRequestDocs {
    public static Schema schema(){
        return new Schema("CreateScreeningRequest");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("movieId").description("영화 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("startTime").description("영화시작시간").type(JsonFieldType.STRING)
        );
    }
}
