package com.rejs.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.epages.restdocs.apispec.Schema;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BusinessExceptionDocs {
    public static Schema schema(){
        return new Schema("problem");
    }

    public static FieldDescriptors fields(){
        return new FieldDescriptors(
                fieldWithPath("type")
                        .description("사전 정의된 예외 타입")
                        .type(JsonFieldType.STRING),
                fieldWithPath("title")
                        .description("예외 이름")
                        .type(JsonFieldType.STRING),
                fieldWithPath("status")
                        .description("예외에 대한 http 상태코드")
                        .type(JsonFieldType.NUMBER),
                fieldWithPath("detail")
                        .description("예외에 대한 자세한 설명")
                        .optional()
                        .type(JsonFieldType.STRING),
                fieldWithPath("instance")
                        .description("예외가 발생한 위치")
                        .type(JsonFieldType.STRING)
        );
    }
}
