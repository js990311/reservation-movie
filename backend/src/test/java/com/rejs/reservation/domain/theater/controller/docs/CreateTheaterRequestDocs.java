package com.rejs.reservation.domain.theater.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CreateTheaterRequestDocs {
    public static Schema schema(){
        return new Schema("theater");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("name").description("영화관이름").type(JsonFieldType.STRING),
                fieldWithPath("rowSize").description("좌석 행 크기").type(JsonFieldType.NUMBER),
                fieldWithPath("colSize").description("좌석 행 크기").type(JsonFieldType.NUMBER)
        );
    }
}
