package com.rejs.reservation.domain.theater.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class TheaterSummaryDocs {
    public static Schema schema(){
        return new Schema("theaterSummary");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("name").description("영화관이름").type(JsonFieldType.STRING)
        );
    }
}
