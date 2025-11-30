package com.rejs.reservation.controller.docs;

import com.epages.restdocs.apispec.FieldDescriptors;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class BaseResponseDocs {
    public static FieldDescriptors baseFields(FieldDescriptors field){
        return new FieldDescriptors(
                fieldWithPath("data").description("메시지 본문").type(JsonFieldType.OBJECT)
        ).andWithPrefix("data.", DocsUtils.mergeFields(field));
    }
}
