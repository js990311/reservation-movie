package com.rejs.reservation.domain.theater.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import com.rejs.reservation.controller.docs.DocsUtils;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class TheaterDtoDocs {
    public static Schema schema(){
        return new Schema("theater");
    }
    
    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("name").description("영화관이름").type(JsonFieldType.STRING),
                fieldWithPath("rowSize").description("영화관의 row 사이즈").type(JsonFieldType.NUMBER),
                fieldWithPath("colSize").description("영화관의 col 사이즈").type(JsonFieldType.NUMBER)
        );
    }

    private static FieldDescriptors seatFields(){
        return new FieldDescriptors(
                fieldWithPath("seatId").description("좌석 이름").type(JsonFieldType.NUMBER),
                fieldWithPath("theaterId").description("영화관 고유번호").type(JsonFieldType.NUMBER),
                fieldWithPath("row").description("영화관 행 위치").type(JsonFieldType.NUMBER),
                fieldWithPath("col").description("영화관 열 위치").type(JsonFieldType.NUMBER)
        );
    }
}
